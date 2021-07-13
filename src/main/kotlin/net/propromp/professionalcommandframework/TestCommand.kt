package net.propromp.professionalcommandframework

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import net.minecraft.server.v1_16_R3.CommandListenerWrapper
import net.propromp.professionalcommandframework.api.annotation.CommandName
import net.propromp.professionalcommandframework.api.annotation.Root
import net.propromp.professionalcommandframework.api.arguments.IntegerArgument
import net.propromp.professionalcommandframework.api.arguments.StringArgument
import org.bukkit.command.CommandSender



@CommandName("test")
class TestCommand {

    @Root
    fun root(sender: CommandSender): Int {
        sender.sendMessage(" a")
        return 1
    }

    @CommandName("foo")
    fun foo(sender: CommandSender): Int {
        sender.sendMessage("a")
        return 1
    }

    @CommandName("test")
    fun testFoo(sender: CommandSender, @IntegerArgument(0, 5) test: Int, @IntegerArgument(0, 5) test2: Int): Int {
        sender.sendMessage("$test $test2")
        return 1
    }

    @CommandName("subcommand")
    class Subcommand {
        @Root
        fun root(sender: CommandSender): Int {
            sender.sendMessage("うんこ！")
            return 50
        }

        @CommandName("test")
        fun test(sender: CommandSender, @IntegerArgument(0,5) test:Int): Int {
            sender.sendMessage(test.toString())
            return 1
        }
    }
}