package net.propromp.professionalcommandframework.api

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.server.v1_16_R3.CommandListenerWrapper

class CommandManager(val dispatcher: CommandDispatcher<CommandListenerWrapper>) {
    fun register(literal:LiteralArgumentBuilder<CommandListenerWrapper>){
        dispatcher.register(literal)
    }
}