package net.propromp.professionalcommandframework.arguments

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import net.propromp.professionalcommandframework.api.arguments.Argument
import net.propromp.professionalcommandframework.api.arguments.LongArgument

class ArgumentLong(private val annotation:LongArgument) :Argument(){
    override fun getBrigadierArgument(): ArgumentType<*> {
        return LongArgumentType.longArg(annotation.min,annotation.max)
    }

    override fun getType(): Class<*> {
        return Long::class.java
    }
}