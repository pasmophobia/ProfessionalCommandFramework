package net.propromp.professionalcommandframework

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.server.v1_16_R3.CommandListenerWrapper
import net.minecraft.server.v1_16_R3.DedicatedServer
import net.propromp.professionalcommandframework.api.AnnotationManager
import net.propromp.professionalcommandframework.api.CommandManager
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_16_R3.CraftServer
import org.bukkit.event.EventHandler
import org.bukkit.plugin.java.JavaPlugin
class ProfessionalCommandFramework : JavaPlugin() {
	companion object {
		lateinit var instance:ProfessionalCommandFramework
	}
	lateinit var dispatcher:CommandDispatcher<CommandListenerWrapper>
	lateinit var manager:CommandManager
	override fun onEnable() {
		instance=this
		val craftServerClass = CraftServer::class.java
		val consoleField = craftServerClass.getDeclaredField("console")
		consoleField.isAccessible=true
		val dedicatedServer = consoleField.get(Bukkit.getServer()) as DedicatedServer
		dispatcher = dedicatedServer.commandDispatcher.dispatcher()
		manager= CommandManager(dispatcher)
		val annotationParser = AnnotationManager(manager)
		annotationParser.register(TestCommand::class.java)

		manager.dispatcher.register(
		LiteralArgumentBuilder.literal<CommandListenerWrapper>("testy")
			.then(
				LiteralArgumentBuilder.literal<CommandListenerWrapper>("test1")
					.then(
						RequiredArgumentBuilder.argument<CommandListenerWrapper,String>("a",StringArgumentType.string())
							.executes {
								it.source.bukkitSender.sendMessage(it.getArgument("a",String::class.java))
								return@executes 1
							}
					)
			)
			.then(
				LiteralArgumentBuilder.literal<CommandListenerWrapper>("testy")
					.then(
						LiteralArgumentBuilder.literal<CommandListenerWrapper>("test1")
							.then(
								RequiredArgumentBuilder.argument<CommandListenerWrapper,String>("a",StringArgumentType.string())
									.executes {
										it.source.bukkitSender.sendMessage(it.getArgument("a",String::class.java))
										return@executes 1
									}
							)
					)
					.executes {
						it.source.bukkitSender.sendMessage("a")
						return@executes 1
					}
			)
			.executes {
				it.source.bukkitSender.sendMessage("a")
				return@executes 1
			}
		)

	}
	override fun onDisable() {

	}
}