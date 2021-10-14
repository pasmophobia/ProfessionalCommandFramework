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
    internal val brigadierDispatcher:CommandDispatcher<Any>
    internal val minecraftDispatcher:Any
    val helpManager = HelpManager(this)
    val annotationManager = AnnotationManager(this)
    val commands = mutableListOf<PcfCommand>()
    init {
        val console = NMS.fromClass(Bukkit.getServer().javaClass).getField(Bukkit.getServer(),"console")!!
        minecraftDispatcher = NMS("MinecraftServer").invokeMethod(console,"getCommandDispatcher")!!
        brigadierDispatcher = NMS.fromClass(minecraftDispatcher.javaClass).getField(minecraftDispatcher,"b") as CommandDispatcher<Any>
    }
    fun registerBrigadier(builder:LiteralArgumentBuilder<Any>):CommandNode<Any> {
        return brigadierDispatcher.register(builder)
    }
    fun register(command:PcfCommand){
        command.register(this)
    }
}