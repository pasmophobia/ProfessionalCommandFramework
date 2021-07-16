package net.propromp.professionalcommandframework

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import net.propromp.professionalcommandframework.api.annotation.CommandName
import net.propromp.professionalcommandframework.api.annotation.CommandPermission
import net.propromp.professionalcommandframework.api.annotation.Root
import net.propromp.professionalcommandframework.api.arguments.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

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
    @CommandName("tptome")
    fun tp(sender: CommandSender,@EntityArgument(EntityArgumentType.MANY_ENTITIES) entity:List<Entity>):Int{
        if(sender is Entity) {
            entity.forEach {
                it.teleport(sender)
            }
        }
        return 1
    }
    @CommandName("give")
    fun give(sender: CommandSender,@ItemStackArgument itemStack:ItemStack,@IntegerArgument(min = 0) amount:Int):Int{
        if(sender is Player){
            sender.inventory.addItem(itemStack.also{it.amount=amount})
            return 1
        }
        return 0
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