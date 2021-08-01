package net.propromp.pcf.api

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.CommandNode
import net.propromp.pcf.nms.NMS
import org.bukkit.Bukkit
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.plugin.Plugin

/**
 * Professional command manager
 *
 * @property brigadierDispatcher command dispatcher
 * @constructor Create empty Command manager
 */
class CommandManager(val plugin: Plugin){
    private val brigadierDispatcher:CommandDispatcher<Any>
    private val minecraftDispatcher:Any
    init {
        val console = NMS.fromClass(Bukkit.getServer().javaClass).getField(Bukkit.getServer(),"console")!!
        minecraftDispatcher = NMS("MinecraftServer").invokeMethod(console,"getCommandDispatcher")!!
        brigadierDispatcher = NMS.fromClass(minecraftDispatcher.javaClass).getField(minecraftDispatcher,"b") as CommandDispatcher<Any>
    }
    fun register(command:PcfCommand){
        val node = brigadierDispatcher.register(command.getLiteralArgumentBuilder())
        val bukkitCommand = NMS.getCraftBukkit("command.VanillaCommandWrapper").getConstructor(NMS.getNMS("CommandDispatcher"),CommandNode::class.java).newInstance(minecraftDispatcher,node) as BukkitCommand
        bukkitCommand.permission=null
        Bukkit.getCommandMap().register(plugin.name,bukkitCommand)
        LiteralArgumentBuilder.literal<Any>("a")
    }
}