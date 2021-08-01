package net.propromp.pcf.api

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.tree.CommandNode
import net.propromp.pcf.nms.NMS
import org.apache.bcel.util.BCELifier
import org.bukkit.Bukkit
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.plugin.Plugin

/**
 * Professional command manager
 *
 * @property dispatcher command dispatcher
 * @constructor Create empty Command manager
 */
class CommandManager(val plugin: Plugin){
    val dispatcher:CommandDispatcher<Any>
    val bukkitDispatcher:Any
    init {
        val console = NMS.fromClass(Bukkit.getServer().javaClass).getField(Bukkit.getServer(),"console")!!
        bukkitDispatcher = NMS("MinecraftServer").invokeMethod(console,"getCommandDispatcher")!!
        dispatcher = NMS.fromClass(bukkitDispatcher.javaClass).getField(bukkitDispatcher,"b") as CommandDispatcher<Any>
    }
    fun register(command:PcfCommand){
        val node = dispatcher.register(command.getLiteralArgumentBuilder())
        val bukkitCommand = NMS.getCraftBukkit("command.VanillaCommandWrapper").getConstructor(CommandDispatcher::class.java,CommandNode::class.java).newInstance(dispatcher,node) as BukkitCommand
        bukkitCommand.permission=null
        Bukkit.getCommandMap().register(plugin.name,bukkitCommand)
        LiteralArgumentBuilder.literal<Any>("a")
    }
}