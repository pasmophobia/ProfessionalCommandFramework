package net.propromp.pcf.api

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.propromp.pcf.api.annotation.*
import net.propromp.pcf.api.annotationparser.ArgumentParser
import net.propromp.pcf.api.annotationparser.ConvertArgumentParser
import net.propromp.pcf.api.annotationparser.CustomArgumentParser
import net.propromp.pcf.nms.NMS
import org.bukkit.command.CommandSender
import java.lang.reflect.Method
import kotlin.Exception

class PcfCommand(val name:String, val permission:String?, val arguments:LinkedHashMap<String, ArgumentParser>, val function: ((CommandContext<Any>)->Int)?, val children:List<PcfCommand>) {
    /**
     * Get literal argument builder
     *
     * @return LiteralArgumentBuilder<Any>
     */
    fun getLiteralArgumentBuilder() :LiteralArgumentBuilder<Any>{
        var literal = LiteralArgumentBuilder.literal<Any>(name)
        //executing
        if(arguments.isEmpty()){
            //with no arguments
            literal = when(function) {
                null->literal
                else->literal.executes{
                    try {
                        function.invoke(it)
                    } catch(e:Exception){
                        e.printStackTrace()
                        throw e
                    }
                }
            }
        } else {
            //with arguments
            var argumentBuilder: RequiredArgumentBuilder<Any, Any>? = null
            for (i in arguments.entries.size - 1 downTo 0) {
                val argumentName = arguments.entries.toList()[i].key
                val argument = arguments.entries.toList()[i].value
                argumentBuilder = when(argumentBuilder){
                    null -> {
                        RequiredArgumentBuilder.argument<Any, Any>(
                            argumentName,
                            argument.getBrigadierArgument() as ArgumentType<Any>
                        ).apply {
                            when(function){
                                null -> {}
                                else -> executes{
                                    try {
                                        function.invoke(it)
                                    } catch(e:Exception){
                                        e.printStackTrace()
                                        throw e
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        RequiredArgumentBuilder.argument<Any, Any>(
                            argumentName,
                            argument.getBrigadierArgument() as ArgumentType<Any>
                        ).then(argumentBuilder)
                    }
                }
                if(argument is CustomArgumentParser<*>){
                    argumentBuilder = argumentBuilder.suggests(argument.suggestionProvider)
                }
            }
            literal = literal.then(argumentBuilder)
        }
        //permission
        literal=when(permission){
            null->literal
            else->literal.requires { (NMS("CommandListenerWrapper").invokeMethod(it,"getBukkitSender") as CommandSender).hasPermission(permission) }
        }
        //children
        children.forEach {
            literal = literal.then(it.getLiteralArgumentBuilder())
        }
        return literal
    }
    companion object {
        /**
         * get PcfCommand from a class
         *
         * @param manager
         * @param clazz
         * @return
         */
        @JvmStatic
        fun fromClass(manager:AnnotationManager,clazz:Class<*>):List<PcfCommand>{
            //read class's annotation
            val commandNames = mutableListOf<String>()
            var permission:String? = null
            clazz.annotations.forEach {
                when(it){
                    is CommandAlias -> {
                        commandNames.addAll(it.name)
                    }
                    is CommandName -> {
                        commandNames.add(it.name)
                    }
                    is CommandPermission -> {
                        permission=it.permission
                    }
                }
            }
            var arguments = linkedMapOf<String,ArgumentParser>()
            var function:((CommandContext<Any>)->Int)? = null
            val children = mutableListOf<PcfCommand>()
            //read methods
            clazz.methods.forEach {method->
                method.annotations.forEach {
                    var isRoot = false
                    var bukkitSender = false
                    val subCommandNames = mutableListOf<String>()
                    var subCommandPermission:String? = null
                    when(it){
                        is BukkitSender -> {
                            bukkitSender=true
                        }
                        is CommandAlias -> {
                            subCommandNames.addAll(it.name)
                        }
                        is CommandName -> {
                            subCommandNames.add(it.name)
                        }
                        is CommandPermission -> {
                            subCommandPermission=it.permission
                        }
                        is Root -> {
                            isRoot = true
                        }
                    }
                    if(isRoot){
                        arguments = getArguments(manager,method)
                        function = getFunction(arguments,method,bukkitSender)
                    } else {
                        subCommandNames.forEach { name->
                            children.add(fromMethod(manager,method,name,subCommandPermission,bukkitSender))
                        }
                    }
                }
            }
            clazz.classes.forEach {
                children.addAll(fromClass(manager,it))
            }
            //construct
            val list = mutableListOf<PcfCommand>()
            commandNames.forEach { name->
                list.add(PcfCommand(name,permission,arguments,function,children))
            }
            return list
        }

        /**
         * get PcfCommand from a method
         *
         * @param manager
         * @param method
         * @param name
         * @param permission
         * @param bukkitSender
         * @return
         */
        @JvmStatic
        fun fromMethod(manager:AnnotationManager,method:Method,name:String,permission:String?,bukkitSender: Boolean):PcfCommand{
            val arguments = getArguments(manager,method)
            val function = getFunction(arguments,method,bukkitSender)
            return PcfCommand(name,permission,arguments,function, mutableListOf())
        }
        @JvmStatic
        internal fun getArguments(manager:AnnotationManager,method: Method):LinkedHashMap<String,ArgumentParser> {
            val map = linkedMapOf<String, ArgumentParser>()
            method.parameters.forEach { parameter ->
                parameter.annotations.forEach { annotation ->
                    manager.argumentMap[annotation.annotationClass.java]?.let { parserClass ->
                        val parser = parserClass.getConstructor(annotation.annotationClass.java)
                            .newInstance(annotation) as ArgumentParser
                        map[parameter.name] = parser
                    }
                }
            }
            return map
        }

        @JvmStatic
        internal fun getFunction(arguments: Map<String,ArgumentParser>, method: Method, bukkitSender:Boolean):((CommandContext<Any>)->Int){
            val obj = method.declaringClass.getConstructor().newInstance()
            return fun (context:CommandContext<Any>):Int {
                val sender = if(bukkitSender){
                    NMS("CommandListenerWrapper").invokeMethod(context.source,"getBukkitSender")
                } else {
                    NMS("CommandListenerWrapper").invokeMethod(context.source,"getBukkitEntity")
                }
                return when(arguments.size){
                    0 -> method.invoke(obj,sender) as Int
                    else -> {
                        val parsedArguments = mutableListOf<Any>()
                        arguments.forEach { (name,argument)->
                            parsedArguments.add(when(argument){
                                is ConvertArgumentParser -> {
                                    argument.convert(context.getArgument(name,Any::class.java),context)
                                }
                                is CustomArgumentParser<*> -> {
                                    argument.convert(context.getArgument(name,Any::class.java),context)
                                }
                                else -> context.getArgument(name,Any::class.java)
                            })
                        }
                        method.invoke(obj,sender,*parsedArguments.toTypedArray()) as Int
                    }
                }
            }
        }
    }
}