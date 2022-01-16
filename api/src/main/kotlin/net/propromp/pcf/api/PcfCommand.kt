package net.propromp.pcf.api

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.CommandNode
import net.propromp.pcf.api.annotation.*
import net.propromp.pcf.api.annotationparser.ArgumentParser
import net.propromp.pcf.api.annotationparser.ConvertArgumentParser
import net.propromp.pcf.api.annotationparser.CustomArgumentParser
import net.propromp.pcf.nms.NMS
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.EntityType
import java.lang.reflect.Method

class PcfCommand(
    val name: String,
    val permission: String?,
    val usage: String?,
    val description: String?,
    val senderType: EntityType?,
    val bukkitSender: Boolean,
    val arguments: LinkedHashMap<String, ArgumentParser>,
    val function: ((CommandContext<Any>) -> Int)?,
    val classInstance: Any?,
    val children: List<PcfCommand>,
    val aliases: List<PcfCommand>?,
    val autoHelp: Boolean
) {
    /**
     * Get literal argument builder
     *
     * @return LiteralArgumentBuilder<Any>
     */
    fun getLiteralArgumentBuilder(commandManager: CommandManager): LiteralArgumentBuilder<Any> {
        var literal = LiteralArgumentBuilder.literal<Any>(name)
        //executing
        if (arguments.isEmpty()) {
            //with no arguments
            literal = when (function) {
                null -> literal
                else -> literal.executes {

                    if (senderType != null) {
                        val sender = if (bukkitSender) {
                            NMS("CommandListenerWrapper").invokeMethod(it.source, "getBukkitSender")
                        } else {
                            NMS("CommandListenerWrapper").invokeMethod(it.source, "getBukkitEntity")
                        } as CommandSender
                        if (senderType.entityClass?.isInstance(sender) == false) {
                            return@executes 0
                        }
                    }
                    try {
                        function.invoke(it)
                    } catch (e: Exception) {
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
                argumentBuilder = when (argumentBuilder) {
                    null -> {
                        RequiredArgumentBuilder.argument<Any, Any>(
                            argumentName,
                            argument.getBrigadierArgument() as ArgumentType<Any>
                        ).apply {
                            when (function) {
                                null -> {
                                }
                                else -> executes {
                                    try {
                                        function.invoke(it)
                                    } catch (e: Exception) {
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
                if (argument is CustomArgumentParser<*>) {
                    argumentBuilder = argumentBuilder.suggests(argument.suggestionProvider)
                }
            }
            literal = literal.then(argumentBuilder)
        }
        //permission
        literal = when (permission) {
            null -> literal
            else -> literal.requires {
                (NMS("CommandListenerWrapper").invokeMethod(
                    it,
                    "getBukkitSender"
                ) as CommandSender).hasPermission(permission)
            }
        }
        //help
        if(autoHelp) {
            literal = literal.then(commandManager.helpManager.getLiteral(this))
        }
        //children
        children.forEach {
            literal = literal.then(it.getLiteralArgumentBuilder(commandManager))
        }
        return literal
    }

    fun getAliasesLiteralBuilder(
        commandManager: CommandManager,
        node: CommandNode<Any>
    ): List<LiteralArgumentBuilder<Any>> {
        val literals = mutableListOf<LiteralArgumentBuilder<Any>>()
        aliases?.forEach {
            var literal = LiteralArgumentBuilder.literal<Any>(it.name)
            literal = literal.redirect(node)
            literals.add(literal)
        }
        return literals
    }

    fun register(commandManager: CommandManager) {
        val node = commandManager.registerBrigadier(getLiteralArgumentBuilder(commandManager))
        getAliasesLiteralBuilder(commandManager, node).forEach {
            commandManager.registerBrigadier(it)
        }
        val bukkitCommand = NMS.getCraftBukkit("command.VanillaCommandWrapper")
            .getConstructor(NMS.getNMS("CommandDispatcher"), CommandNode::class.java)
            .newInstance(commandManager.minecraftDispatcher, node) as BukkitCommand
        bukkitCommand.permission = null
        if (usage != null) {
            bukkitCommand.usage = usage
        }
        if (description != null) {
            bukkitCommand.description = description
        }
        bukkitCommand.aliases = aliases?.map { it.name } ?: listOf()
        Bukkit.getCommandMap().register(commandManager.plugin.name, bukkitCommand)
        commandManager.commands.add(this)
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
        fun fromClass(manager: AnnotationManager, instance: Any): PcfCommand {
            val commandManager = manager.manager
            val clazz = instance::class.java
            //read class's annotation
            var name = ""
            val aliases = mutableListOf<String>()
            var permission: String? = null
            var rootSenderType: EntityType? = null
            var rootBukkitSender = false
            var rootDescription:String? = null
            var rootUsage:String? = null
            var autoHelp = false
            clazz.annotations.forEach {
                when (it) {
                    is CommandAlias -> {
                        aliases.addAll(it.name)
                    }
                    is CommandName -> {
                        name = it.name
                    }
                    is CommandPermission -> {
                        permission = it.permission
                    }
                    is SenderType -> {
                        rootSenderType = it.type
                    }
                    is BukkitSender -> {
                        rootBukkitSender = true
                    }
                    is CommandUsage -> {
                        rootUsage = it.usage
                    }
                    is CommandDescription -> {
                        rootDescription = it.description
                    }
                    is AutoHelp -> {
                        autoHelp = true
                    }
                }
            }
            var arguments = linkedMapOf<String, ArgumentParser>()
            var function: ((CommandContext<Any>) -> Int)? = null
            val children = mutableListOf<PcfCommand>()
            //read methods
            clazz.methods.forEach { method ->
                var isRoot = false
                var bukkitSender = rootBukkitSender
                var senderType = rootSenderType
                val subCommandNames = mutableListOf<String>()
                var subCommandPermission: String? = null
                var usage:String? = null
                var description:String? = null
                method.annotations.forEach {
                    when (it) {
                        is BukkitSender -> {
                            bukkitSender = true
                        }
                        is SenderType -> {
                            senderType = it.type
                        }
                        is CommandAlias -> {
                            subCommandNames.addAll(it.name)
                        }
                        is CommandName -> {
                            subCommandNames.add(it.name)
                        }
                        is CommandPermission -> {
                            subCommandPermission = it.permission
                        }
                        is Root -> {
                            isRoot = true
                        }
                        is CommandUsage -> {
                            usage = it.usage
                        }
                        is CommandDescription -> {
                            description = it.description
                        }
                    }
                }
                if (isRoot) {
                    arguments = getArguments(manager, method)
                    function = getFunction(instance, arguments, method, bukkitSender)
                } else {
                    subCommandNames.forEach { name ->
                        children.add(
                            fromMethod(
                                instance,
                                manager,
                                method,
                                name,
                                subCommandPermission,
                                usage,
                                description,
                                senderType,
                                bukkitSender
                            )
                        )
                    }
                }
            }
            clazz.classes.forEach {
                try {
                    children.add(fromClass(manager, it.getConstructor().newInstance()))
                } catch(_:Exception) {}
            }
            //construct
            val list = mutableListOf<PcfCommand>()
            aliases.forEach { it ->
                list.add(
                    PcfCommand(
                        it,
                        permission,
                        rootUsage,
                        rootDescription,
                        rootSenderType,
                        rootBukkitSender,
                        arguments,
                        function,
                        instance,
                        children, null, false
                    )
                )
            }
            return PcfCommand(
                name,
                permission,
                rootUsage,
                rootDescription,
                rootSenderType,
                rootBukkitSender,
                arguments,
                function,
                instance,
                children,
                list,
                autoHelp
            )
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
        fun fromMethod(
            instance: Any?,
            manager: AnnotationManager,
            method: Method,
            name: String,
            permission: String?,
            usage: String?,
            description: String?,
            senderType: EntityType?,
            bukkitSender: Boolean
        ): PcfCommand {
            val arguments = getArguments(manager, method)
            val function = getFunction(instance, arguments, method, bukkitSender)
            return PcfCommand(
                name,
                permission,
                usage,
                description,
                senderType,
                bukkitSender,
                arguments,
                function,
                instance,
                mutableListOf(),
                null, false
            )
        }

        @JvmStatic
        internal fun getArguments(manager: AnnotationManager, method: Method): LinkedHashMap<String, ArgumentParser> {
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
        internal fun getFunction(
            instance: Any?,
            arguments: Map<String, ArgumentParser>,
            method: Method,
            bukkitSender: Boolean
        ): ((CommandContext<Any>) -> Int) {
            val obj = instance ?: method.declaringClass.getConstructor().newInstance()
            return fun(context: CommandContext<Any>): Int {
                val sender = if (bukkitSender) {
                    NMS("CommandListenerWrapper").invokeMethod(context.source, "getBukkitSender")
                } else {
                    NMS("CommandListenerWrapper").invokeMethod(context.source, "getBukkitEntity")
                }
                val res = when (arguments.size) {
                    0 -> method.invoke(obj, sender)
                    else -> {
                        val parsedArguments = mutableListOf<Any>()
                        arguments.forEach { (name, argument) ->
                            parsedArguments.add(
                                when (argument) {
                                    is ConvertArgumentParser -> {
                                        argument.convert(context.getArgument(name, Any::class.java), context)
                                    }
                                    is CustomArgumentParser<*> -> {
                                        argument.convert(context.getArgument(name, Any::class.java), context)
                                    }
                                    else -> context.getArgument(name, Any::class.java)
                                }
                            )
                        }
                        method.invoke(obj, sender, *parsedArguments.toTypedArray())
                    }
                }
                return if (res is Int) {
                    res
                } else {
                    1
                }
            }
        }
    }
}