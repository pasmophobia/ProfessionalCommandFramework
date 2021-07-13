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

/**
 * Annotation manager
 *
 * @property manager
 * @constructor Create empty Annotation manager
 */
class AnnotationManager(val manager: CommandManager) {
    private val argumentMap = mutableMapOf<Class<out Annotation>, Class<out Argument>>()

    init {
        registerArgument(IntegerArgument::class.java, ArgumentInteger::class.java)
    }

    /**
     * Register argument
     *
     * @param annotationClass annotation class
     * @param argumentClass argument class
     */
    fun registerArgument(annotationClass: Class<out Annotation>, argumentClass: Class<out Argument>) {
        argumentMap[annotationClass] = argumentClass
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
            var root = LiteralArgumentBuilder.literal<CommandListenerWrapper>(name)
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
                        is AbsoluteSender -> {
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
            return root
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
                    val argument =
                        argumentMap[annotation.annotationClass.java]!!.getConstructor(annotation.annotationClass.java)
                            .newInstance(annotation)
                    arguments[parameter.name] = argument
                }
            }
        }
        //literal build
        if (name != null) {
            if (arguments.isNotEmpty()) {
                var argumentBuilder: RequiredArgumentBuilder<CommandListenerWrapper, Any>? = null
                for (i in arguments.entries.size - 1 downTo 0) {
                    val argumentName = arguments.entries.toList()[i].key
                    val argument = arguments.entries.toList()[i].value
                    argumentBuilder=if(argumentBuilder==null){
                        RequiredArgumentBuilder.argument<CommandListenerWrapper, Any>(
                            argumentName,
                            argument.getBrigadierArgument() as ArgumentType<Any>
                        ).executes(getCommand(method, arguments, absoluteSender))
                    } else {
                        RequiredArgumentBuilder.argument<CommandListenerWrapper, Any>(
                            argumentName,
                            argument.getBrigadierArgument() as ArgumentType<Any>
                        ).then(argumentBuilder)
                    }
                }
                return LiteralArgumentBuilder.literal<CommandListenerWrapper>(name).then(argumentBuilder)
            } else {
                return LiteralArgumentBuilder.literal<CommandListenerWrapper>(name)
                    .executes(getCommand(method, absoluteSender))
            }
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
                throw e
            }
        }
    }
}