package net.propromp.professionalcommandframework.arguments

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import net.propromp.professionalcommandframework.api.arguments.Argument
import net.propromp.professionalcommandframework.api.arguments.StringArgument

class ArgumentString(private val annotation:StringArgument): Argument() {
    override fun getBrigadierArgument(): ArgumentType<*> {
        return when(annotation.type){
            net.propromp.professionalcommandframework.api.arguments.StringArgumentType.SINGLE_WORD -> StringArgumentType.word()
            net.propromp.professionalcommandframework.api.arguments.StringArgumentType.GREEDY -> StringArgumentType.greedyString()
        }
    }

    override fun getType(): Class<*> {
        return String::class.java
    }
}