package net.propromp.pcf.api

import com.mojang.brigadier.CommandDispatcher
import net.propromp.pcf.nms.NMS
import org.bukkit.Bukkit
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
}