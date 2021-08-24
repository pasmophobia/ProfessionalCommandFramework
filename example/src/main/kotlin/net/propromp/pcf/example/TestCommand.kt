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
class TestCommand {
    /**
     * /test
     *
     * @param sender command sender will be assined
     * @return Brigadier return value
     */
    @Root
    fun root(sender: CommandSender): Int {
        sender.sendMessage(" a")
        return 1
    }

    /**
     * /test foo
     * @BukkitSender means that when `/execute as <entity> run <command>` is executed,
     * the executor of `/execute as <entity> run <command>` will be assigned to sender.
     * @param sender
     * The return value can be omitted.
     */
    @CommandName("foo")
    @BukkitSender
    fun foo(sender: CommandSender): Int {
        sender.sendMessage("a")
    }

    /**
     * /test test <test> <test2>
     *
     * @param sender
     * @param test
     * @param test2
     * @return
     */
    @CommandName("test")
    fun testFoo(sender: CommandSender, @IntegerArgument(0, 5) test: Int, @IntegerArgument(0, 5) test2: Int): Int {
        sender.sendMessage("$test*$test2=${test+test2}")
        return test+test2
    }

    /**
     * /test tptome <entity>
     *
     * @param sender
     * @param entity
     * @return
     */
    @CommandName("tptome")
    fun tp(sender: CommandSender,@EntityArgument(EntityArgumentType.MANY_ENTITIES) entity:List<Entity>):Int{
        if(sender is Entity) {
            entity.forEach {
                it.teleport(sender)
            }
        }
        return 1
    }

    /**
     * /test give <itemStack> <amount>
     *
     * @param sender
     * @param itemStack
     * @param amount
     * @return
     */
    @CommandName("give")
    fun give(sender: CommandSender,@ItemStackArgument itemStack:ItemStack,@IntegerArgument(min = 0) amount:Int):Int{
        if(sender is Player){
            sender.inventory.addItem(itemStack.also{it.amount=amount})
            return 1
        }
        return 0
    }

    /**
     * /test iamop
     * If you are not an op,the command will be not displayed in suggestion
     *
     * @param sender
     * @return
     */
    @CommandName("iamop")
    @CommandPermission("op")
    @SenderType(EntityType.PLAYER)
    fun iAmOp(sender: Player){
        Bukkit.broadcast(Component.text("[${sender.name}] I am OP!!"))
    }
    /**
     * /test eat <food>
     * (This command uses [net.propromp.pcf.example.FoodArgumentParser].)
     */
    @CommandName("eat")
    fun eat(sender: Player,@FoodArgument food: Food){
        Bukkit.broadcast(Component.text("[${sender.name}] I ate $food!!"))
        Bukkit.getOnlinePlayers().forEach {
            it.playSound(it.location, Sound.ENTITY_PLAYER_BURP,1f,1f)
        }
    }


    /**
     * Subcommand
     */
    @CommandName("subcommand")
    @CommandAlias(["sc"])
    class Subcommand {
        /**
         * /test subcommand
         */
        @Root
        fun root(sender: CommandSender) {
            sender.sendMessage("/test subcommand!!")
            return 1
        }

        /**
         * /test subcommand test <test>
         */
        @CommandName("test")
        @CommandAlias(["ts"])
        fun test(sender: CommandSender, @StringArgument(StringType.SINGLE_STRING) test:String) {
            sender.sendMessage(test)
        }
    }
}
