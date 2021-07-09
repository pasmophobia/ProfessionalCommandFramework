package net.propromp.professionalcommandframework.api

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.server.v1_16_R3.*
import net.propromp.professionalcommandframework.api.annotation.AbsoluteSender
import net.propromp.professionalcommandframework.api.annotation.CommandName
import net.propromp.professionalcommandframework.api.annotation.Root
import net.propromp.professionalcommandframework.api.arguments.Argument
import net.propromp.professionalcommandframework.api.arguments.IntegerArgument
import net.propromp.professionalcommandframework.api.exception.AnnotationParseException
import net.propromp.professionalcommandframework.arguments.ArgumentInteger
import org.bukkit.command.CommandSender
import java.lang.reflect.*

class AnnotationManager(val manager: CommandManager) {
    private val argumentMap = mutableMapOf<Class<out Annotation>, Class<out Argument>>()

    init {
        registerArgument(IntegerArgument::class.java, ArgumentInteger::class.java)
    }

    fun registerArgument(annotationClass: Class<out Annotation>, argumentClass: Class<out Argument>) {
        argumentMap[annotationClass] = argumentClass
    }

    fun register(root: Class<*>) {
        root.annotations.forEach { annotation ->
            when (annotation) {
                is CommandName -> {
                    manager.dispatcher.register(getLiteral(root))
                    return
                }
            }
        }
        throw AnnotationParseException("No @CommandName!")
    }

    private fun getLiteral(clazz: Class<*>): LiteralArgumentBuilder<CommandListenerWrapper> {
        var name: String? = null
        clazz.annotations.forEach { annotation ->
            if (annotation is CommandName) {
                name = annotation.name
            }
        }
        if (name != null) {
            var literal = LiteralArgumentBuilder.literal<CommandListenerWrapper>(name)
            clazz.methods.forEach { method ->
                var root = false
                var absoluteSender = false
                method.annotations.forEach { annotation ->
                    when (annotation) {
                        is CommandName -> {
                            literal = literal.then(getLiteral(method))
                        }
                        is Root -> {
                            root = true
                        }
                        is AbsoluteSender -> {
                            absoluteSender = true
                        }
                    }
                }
                if(root) {
                    literal = literal.executes(getCommand(method, absoluteSender))
                }
            }
            clazz.classes.filter { method ->
                method.annotations.forEach { annotation ->
                    if (annotation.javaClass == CommandName::class.java) {
                        true
                    }
                }
                false
            }.forEach { method ->
                literal = literal.then(getLiteral(method))
            }
            return literal
        } else {
            throw AnnotationParseException("No @CommandName!")
        }
    }

    private fun getLiteral(method: Method): LiteralArgumentBuilder<CommandListenerWrapper> {
        var name: String? = null
        var absoluteSender = false
        val arguments = linkedMapOf<String, Argument>()
        method.annotations.forEach { annotation ->
            when (annotation) {
                is CommandName -> name = annotation.name
                is AbsoluteSender -> absoluteSender = true
            }
        }

        if (method.returnType != Int::class.java) {
            throw AnnotationParseException("return type must be int")
        }

        if (method.parameters[0].type != CommandSender::class.java) {
            throw AnnotationParseException("first argument must be CommandSender")
        }
        mutableListOf(method.parameters).removeAt(0).forEach { parameter ->
            parameter.annotations.forEach { annotation ->
                if (argumentMap.containsKey(annotation.annotationClass.java)) {
                    val argument = argumentMap[annotation.annotationClass.java]!!.getConstructor(annotation.annotationClass.java)
                        .newInstance(annotation)
                    arguments[parameter.name] = argument
                }
            }
        }
        //literal build
        if (name != null) {
            if (arguments.isNotEmpty()) {
                var argumentBuilder: RequiredArgumentBuilder<CommandListenerWrapper, Any>? = null
                arguments.forEach { (name, argument) ->
                    argumentBuilder = if (argumentBuilder == null) {
                        RequiredArgumentBuilder.argument(
                            name,
                            argument.getBrigadierArgument() as ArgumentType<Any>
                        )
                    } else {
                        argumentBuilder!!.then(
                            RequiredArgumentBuilder.argument(
                                name,
                                argument.getBrigadierArgument() as ArgumentType<Any>
                            )
                        )
                    }
                }
                return LiteralArgumentBuilder.literal<CommandListenerWrapper>(name).then(argumentBuilder!!.executes(getCommand(method, arguments, absoluteSender)))
            } else {
                return LiteralArgumentBuilder.literal<CommandListenerWrapper>(name).executes(getCommand(method, absoluteSender))
            }
        } else {
            throw AnnotationParseException("No @CommandName!")
        }
    }

    private fun getCommand(
        method: Method,
        arguments: LinkedHashMap<String, Argument>,
        absoluteSender: Boolean
    ): Command<CommandListenerWrapper> {
        return Command { context ->
            try {
                val sender = if (absoluteSender) {
                    context.source.bukkitSender
                } else {
                    context.source.bukkitEntity
                }
                return@Command method.invoke(
                    method.declaringClass.getConstructor().newInstance(),
                    sender,
                    *arguments.map { context.getArgument(it.key, it.value.getType()) }.toTypedArray()
                ) as Int
            } catch (e: Exception) {
                e.printStackTrace()
                return@Command 0
            }
        }
    }

    private fun getCommand(method: Method, absoluteSender: Boolean): Command<CommandListenerWrapper> {
        return Command { context ->
            try {
                val sender = if (absoluteSender) {
                    context.source.bukkitSender
                } else {
                    context.source.bukkitEntity
                }
                return@Command method.invoke(method.declaringClass.getConstructor().newInstance(), sender) as Int
            } catch (e: Exception) {
                e.printStackTrace()
                return@Command 0
            }
        }
    }
}