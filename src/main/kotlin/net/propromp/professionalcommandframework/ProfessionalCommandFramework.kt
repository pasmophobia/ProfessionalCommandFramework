package net.propromp.professionalcommandframework

import net.propromp.professionalcommandframework.api.AnnotationManager
import net.propromp.professionalcommandframework.api.CommandManager
import org.bukkit.plugin.java.JavaPlugin

class ProfessionalCommandFramework : JavaPlugin() {
    companion object {
        lateinit var instance: ProfessionalCommandFramework

        @JvmStatic
        val FAILURE = 0

        @JvmStatic
        val SUCCESS = 1
    }

    lateinit var manager: CommandManager
    lateinit var annotationManager:AnnotationManager
    override fun onEnable() {
        instance = this
        manager = CommandManager(this)
        annotationManager = AnnotationManager(manager)
        annotationManager.register(TestCommand::class.java)
    }

    override fun onDisable() {
//        annotationManager.unregisterAll()
    }
}