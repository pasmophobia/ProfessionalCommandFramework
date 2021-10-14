package net.propromp.pcf.example

import net.kyori.adventure.text.Component
import net.propromp.pcf.api.annotation.*
import net.propromp.pcf.api.arguments.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * declares a command
 */
@CommandName("test")
@CommandAlias(["tes","ts"])
@AutoHelp
@CommandUsage("/test help")
@CommandDescription("A test command.")
class TestCommand {
    /**
     * /test
     *
     * @param sender command sender
     * @return return value commonly used by Vanilla commands(you can use either [net.propromp.pcf.ProfessionalCommandFramework.FAILURE] or [net.propromp.pcf.ProfessionalCommandFramework.SUCCESS])
     */
    @Root
    fun root(sender: CommandSender): Int {
        sender.sendMessage(" a")
        return 1
    }

    /**
     * /test foo
     * @BukkitSender:Sender will be a sender of the command when you execute (/execute as ...) command
     * @param sender
     * @return
     */
    @CommandName("foo")
    @BukkitSender
    fun foo(sender: CommandSender): Int {
        sender.sendMessage("a")
        return 1
    }

    /**
     * /test test <test> <test2>
     * You can add arguments by adding Argument annotation to the parameter.
     *
     * @param sender
     * @param test
     * @param test2
     * @return
     */
    @CommandName("test")
    @CommandDescription("foo!")
    fun testFoo(sender: CommandSender, @IntegerArgument(0, 5) test: Int, @IntegerArgument(0, 5) test2: Int): Int {
        sender.sendMessage("$test*$test2=${test+test2}")
        return test+test2
    }

    /**
     * /test tptome <entity>
     * There are various types of argument.
     *
     * @param sender
     * @param entity
     * @return
     */
    @CommandName("tptome")
    @CommandDescription("teleport entities to your location.")
    fun tp(sender: CommandSender,@EntityArgument(EntityArgumentType.MANY_ENTITIES) entity:List<Entity>):Int{
        if(sender is Entity) {
            entity.forEach {
                it.teleport(sender)
            }
        }
        return 1
    }

    /**
     * You can check sender's permissions.
     *
     * @param sender
     * @return
     */
    @CommandName("iamop")
    @CommandPermission("op")
    @SenderType(EntityType.PLAYER)
    @CommandDescription("Are you an op?")
    fun iAmOp(sender: Player):Int{
        Bukkit.broadcast(Component.text("[${sender.name}] I am OP!!"))
        return 1
    }

    /**
     * You can also add a custom argument type.
     * look at [net.propromp.pcf.example.FoodArgument]
     *
     * @param sender
     * @param food
     * @return
     */
    @CommandName("eat")
    @CommandDescription("eat a food.")
    fun eat(sender: Player,@FoodArgument food: Food):Int{
        Bukkit.broadcast(Component.text("[${sender.name}] I ate $food!!"))
        Bukkit.getOnlinePlayers().forEach {
            it.playSound(it.location, Sound.ENTITY_PLAYER_BURP,1f,1f)
        }
        return 1
    }

    var count = 0
    /**
     * You can also use variables!
     *
     * @param sender
     */
    @CommandName("count")
    @CommandDescription("count")
    fun count(sender: CommandSender) {
        count++
        sender.sendMessage(count.toString())
    }

    /**
     * Subcommand
     */
    @CommandName("subcommand")
    @CommandAlias(["sc"])
    @CommandDescription("sub command!")
    class Subcommand {
        /**
         * /test subcommand
         */
        @Root
        fun root(sender: CommandSender): Int {
            sender.sendMessage("/test subcommand!!")
            return 1
        }

        /**
         * /test subcommand test <test>
         */
        @CommandName("test")
        @CommandAlias(["ts"])
        @CommandDescription("sub test.")
        fun test(sender: CommandSender, @StringArgument(StringType.SINGLE_STRING) test:String): Int {
            sender.sendMessage(test)
            return 1
        }
    }
}