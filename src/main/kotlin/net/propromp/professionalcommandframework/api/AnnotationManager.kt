package net.propromp.professionalcommandframework.api

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import net.propromp.professionalcommandframework.api.annotation.BukkitSender
import net.propromp.professionalcommandframework.api.annotation.CommandName
import net.propromp.professionalcommandframework.api.annotation.CommandPermission
import net.propromp.professionalcommandframework.api.annotation.Root
import net.propromp.professionalcommandframework.api.annotationparser.AnnotationParser
import net.propromp.professionalcommandframework.api.annotationparser.ConvertAnnotationParser
import net.propromp.professionalcommandframework.api.arguments.*
import net.propromp.professionalcommandframework.api.exception.AnnotationParseException
import net.propromp.professionalcommandframework.arguments.*
import net.propromp.professionalcommandframework.arguments.EntityAnnotationParser
import net.propromp.professionalcommandframework.nms.NMS
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.SimpleCommandMap
import java.lang.reflect.*

/**
 * Annotation manager
 *
 * @property manager
 * @constructor Create empty Annotation manager
 */
class AnnotationManager(val manager: CommandManager) {
    private val argumentMap = mutableMapOf<Class<out Annotation>, Class<out AnnotationParser>>()
    internal val commands = mutableListOf<LiteralCommandNode<Any>>()

    init {
        registerArgument(BooleanArgument::class.java,BooleanAnnotationParser::class.java)
        registerArgument(DoubleArgument::class.java,DoubleAnnotationParser::class.java)
        registerArgument(FloatArgument::class.java,FloatAnnotationParser::class.java)
        registerArgument(IntegerArgument::class.java, IntegerAnnotationParser::class.java)
        registerArgument(LongArgument::class.java,LongAnnotationParser::class.java)
        registerArgument(StringArgument::class.java,StringAnnotationParser::class.java)
        registerArgument(EntityArgument::class.java,EntityAnnotationParser::class.java)
        registerArgument(ItemStackArgument::class.java,ItemStackAnnotationParser::class.java)
    }

    /**
     * Register argument
     *
     * @param annotationClass annotation class
     * @param annotationParserClass argument class
     */
    fun registerArgument(annotationClass: Class<out Annotation>, annotationParserClass: Class<out AnnotationParser>) {
        argumentMap[annotationClass] = annotationParserClass
    }

    /**
     * Register a command
     *
     * @param root class
     */
    fun register(root: Class<*>) {
        root.annotations.forEach { annotation ->
            when (annotation) {
                is CommandName -> {
                    val node = manager.dispatcher.register(getLiteral(root))
                    val commandMap = Bukkit.getCommandMap() as SimpleCommandMap
                    val bukkitCommand = NMS(NMS.getCraftBukkit("command.VanillaCommandWrapper")).clazz.getConstructor(NMS.getNMS("CommandDispatcher"),CommandNode::class.java).newInstance(manager.bukkitDispatcher,node) as org.bukkit.command.Command
                    bukkitCommand.permission=null
                    commandMap.register("pcf",bukkitCommand)
                    commands.add(node)
                    return
                }
            }
        }
        throw AnnotationParseException("No @CommandName!")
    }

    private fun getLiteral(clazz: Class<*>): LiteralArgumentBuilder<Any> {
        var name: String? = null
        var permission:String? = null
        clazz.annotations.forEach { annotation ->
            when(annotation){
                is CommandName -> name=annotation.name
                is CommandPermission -> permission=annotation.permission
            }
        }
        if (name != null) {
            var root = LiteralArgumentBuilder.literal<Any>(name)
            clazz.methods.forEach { method ->
                var hasRootAnnotation = false
                var absoluteSender = false
                method.annotations.forEach { annotation ->
                    when (annotation) {
                        is CommandName -> {
                            root = root.then(getLiteral(method))
                        }
                        is Root -> {
                            hasRootAnnotation = true
                        }
                        is BukkitSender -> {
                            absoluteSender = true
                        }
                    }
                }
                if (hasRootAnnotation) {
                    root = root.executes(getCommand(method, absoluteSender))
                }
            }
            clazz.classes.forEach { childClass ->
                childClass.annotations.forEach { annotation ->
                    if (annotation is CommandName) {
                        root = root.then(getLiteral(childClass))
                    }
                }
            }
            if(permission!=null) {
                root = root.requires {
                    (NMS("CommandListenerWrapper").invokeMethod(
                        it,
                        "getBukkitSender"
                    ) as CommandSender).hasPermission(permission!!)
                }
            }
            return root
        } else {
            throw AnnotationParseException("No @CommandName!")
        }
    }

    private fun getLiteral(method: Method): LiteralArgumentBuilder<Any> {
        var name: String? = null
        var absoluteSender = false
        var permission:String? = null
        val arguments = linkedMapOf<String, AnnotationParser>()
        method.annotations.forEach { annotation ->
            when (annotation) {
                is CommandName -> name = annotation.name
                is BukkitSender -> absoluteSender = true
                is CommandPermission -> permission = annotation.permission
            }
        }

        if (method.returnType != Int::class.java) {
            throw AnnotationParseException("return type must be int but it is ${method.returnType}")
        }

        if (method.parameters[0].type != CommandSender::class.java) {
            throw AnnotationParseException("first argument must be CommandSender")
        }
        mutableListOf(method.parameters).removeAt(0).forEach { parameter ->
            parameter.annotations.forEach { annotation ->
                if (argumentMap.containsKey(annotation.annotationClass.java)) {
                    val argument =
                        argumentMap[annotation.annotationClass.java]!!.getConstructor(annotation.annotationClass.java)
                            .newInstance(annotation)
                    arguments[parameter.name] = argument
                }
            }
        }
        //literal build
        if (name != null) {
            var literal = if (arguments.isNotEmpty()) {
                var argumentBuilder: RequiredArgumentBuilder<Any, Any>? = null
                for (i in arguments.entries.size - 1 downTo 0) {
                    val argumentName = arguments.entries.toList()[i].key
                    val argument = arguments.entries.toList()[i].value
                    argumentBuilder=if(argumentBuilder==null){
                        RequiredArgumentBuilder.argument<Any, Any>(
                            argumentName,
                            argument.getBrigadierArgument() as ArgumentType<Any>
                        ).executes(getCommand(method, arguments, absoluteSender))
                    } else {
                        RequiredArgumentBuilder.argument<Any, Any>(
                            argumentName,
                            argument.getBrigadierArgument() as ArgumentType<Any>
                        ).then(argumentBuilder)
                    }
                }
                LiteralArgumentBuilder.literal<Any>(name).then(argumentBuilder)
            } else {
                LiteralArgumentBuilder.literal<Any>(name).executes(getCommand(method, absoluteSender))
            }
            if(permission!=null) {
                literal= literal.requires {
                    (NMS("CommandListenerWrapper").invokeMethod(
                        it,
                        "getBukkitSender"
                    ) as CommandSender).hasPermission(permission!!)
                }
            }
            return literal
        } else {
            throw AnnotationParseException("No @CommandName!")
        }
    }

    /**
     * Get command from a method
     *
     * @param method
     * @param arguments argumentName,argument
     * @param absoluteSender
     * @return brigadier command
     */
    private fun getCommand(
        method: Method,
        arguments: LinkedHashMap<String, AnnotationParser>,
        absoluteSender: Boolean
    ): Command<Any> {
        return Command { context ->
            try {
                val sender = if (absoluteSender) {
                    NMS("CommandListenerWrapper").invokeMethod(context.source,"getBukkitSender")
                } else {
                    NMS("CommandListenerWrapper").invokeMethod(context.source,"getBukkitEntity")
                }
                val argumentArray = mutableListOf<Any>()
                arguments.forEach { (name,argument) ->
                    argumentArray+=if(argument is ConvertAnnotationParser) {
                        argument.convert(context.getArgument(name, argument.getSourceType()), context)
                    } else {
                        context.getArgument(name,argument.getType())
                    }

                }
                return@Command method.invoke(
                    method.declaringClass.getConstructor().newInstance(),
                    sender,
                    *argumentArray.toTypedArray()
                ) as Int
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }

    /**
     * Get command from a method
     *
     * @param method
     * @param absoluteSender
     * @return brigadier command
     */
    private fun getCommand(method: Method, absoluteSender: Boolean): Command<Any> {
        return Command<Any> { context ->
            try {
                val sender = if (absoluteSender) {
                    NMS("CommandListenerWrapper").invokeMethod(context.source,"getBukkitSender")
                } else {
                    NMS("CommandListenerWrapper").invokeMethod(context.source,"getBukkitEntity")
                }
                return@Command method.invoke(method.declaringClass.getConstructor().newInstance(), sender) as Int
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }
    internal fun unregisterAll() {
        ArrayList(commands).forEach {
            NMS(CommandNode::class.java).invokeMethod(manager.dispatcher.root,"removeCommand",it.name)
            commands.remove(it)
        }
    }
}