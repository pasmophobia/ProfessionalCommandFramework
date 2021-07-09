package net.propromp.professionalcommandframework

import net.propromp.professionalcommandframework.api.annotation.CommandName
import net.propromp.professionalcommandframework.api.annotation.Root
import net.propromp.professionalcommandframework.api.arguments.IntegerArgument
import net.propromp.professionalcommandframework.arguments.ArgumentInteger
import org.bukkit.command.CommandSender
@CommandName("a")
class TestCommand {
    @Root
    fun root(sender:CommandSender):Int{
        sender.sendMessage(" a")
        return 1
    }
    @CommandName("foo")
    fun foo(sender:CommandSender):Int{
        sender.sendMessage("a")
        return 1
    }
    @CommandName("test")
    fun testFoo(sender:CommandSender,@IntegerArgument(0,5) test:Int):Int{
        sender.sendMessage(test.toString())
        return 1
    }
}