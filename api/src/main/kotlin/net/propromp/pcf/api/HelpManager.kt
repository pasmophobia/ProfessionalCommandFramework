package net.propromp.pcf.api

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.propromp.pcf.nms.NMS
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

class HelpManager(manager: CommandManager) {
    /**
     * Processor
     * # example
     * PcfCommand(
     *      name="test",
     *      ...,
     *      usage="/test <person>",
     *      description="hello",
     *      ...,
     *      children=[PcfCommand(name="test2")],
     *      aliases=[PcfCommand(name="ts")]
     * )
     * processor.invoke(name,description,usage,aliases.map{...},children.associate{...})
     */
    var processor: (root: String, rootDescription: String, aliases: List<String>?, usageDescriptionMap: Map<String, String>) -> String =
        { root, rootDescription, aliases, usageDescriptionMap ->
            var res = ""
            res += "${ChatColor.YELLOW}---------"
            res += "${ChatColor.WHITE} Help: /$root "
            res += "${ChatColor.YELLOW}----------------------------\n"
            res += "${ChatColor.GOLD}Description: ${ChatColor.WHITE}${rootDescription}\n"
            if (aliases != null) {
                res += "${ChatColor.GOLD}Aliases: ${ChatColor.WHITE}"
                aliases.forEach {
                    res += "$it,"
                }
                res += "\n"
            }
            res += "${ChatColor.GOLD}Subcommands: \n${ChatColor.WHITE}"
            usageDescriptionMap.forEach { (usage, description) ->
                res += "$usage${ChatColor.GRAY} : ${ChatColor.WHITE}$description\n"
            }
            res
        }

    fun process(pcfCommand: PcfCommand): String {
        return processor.invoke(
            pcfCommand.name, pcfCommand.description ?: "", pcfCommand.aliases?.map { it.name },
            getUsageDescriptionMap(pcfCommand)
        )
    }

    fun getUsageDescriptionMap(pcfCommand: PcfCommand, parentCommand: String? = null,root:Boolean=true): Map<String, String> {
        val map = mutableMapOf<String, String>()
        pcfCommand.children.forEach {
            val parent = (parentCommand?.plus(" ") ?: "") + pcfCommand.name
            getUsageDescriptionMap(it, parentCommand = parent,root=false).forEach {
                map[it.key] = it.value
            }
        }
        if(!root) {
            val usage = getUsage(parentCommand, pcfCommand)
            map[usage] = pcfCommand.description ?: "A command."
        }
        return map
    }

    fun getLiteral(pcfCommand: PcfCommand): LiteralArgumentBuilder<Any> {
        var literal = LiteralArgumentBuilder.literal<Any>("help")
        if (pcfCommand.permission != null) {
            literal = literal.requires {
                (NMS("CommandListenerWrapper").invokeMethod(
                    it,
                    "getBukkitSender"
                ) as CommandSender).hasPermission(pcfCommand.permission)
            }
        }
        literal = literal.executes { context ->
            val sender = if (pcfCommand.bukkitSender) {
                NMS("CommandListenerWrapper").invokeMethod(context.source, "getBukkitSender")
            } else {
                NMS("CommandListenerWrapper").invokeMethod(context.source, "getBukkitEntity")
            } as CommandSender
            sender.sendMessage(process(pcfCommand))
            return@executes 1
        }
        return literal
    }

    fun getUsage(parent: String?, pcfCommand: PcfCommand): String {
        var res = "/"
        if (parent != null) {
            res += parent
            res += " "
        }
        res += pcfCommand.name
        pcfCommand.arguments.forEach {
            res += " <${it.key}>"
        }
        return res
    }
}