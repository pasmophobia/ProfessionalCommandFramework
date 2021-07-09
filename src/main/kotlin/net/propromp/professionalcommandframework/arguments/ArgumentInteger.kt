package net.propromp.professionalcommandframework.arguments

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import net.propromp.professionalcommandframework.api.arguments.Argument
import net.propromp.professionalcommandframework.api.arguments.IntegerArgument

class ArgumentInteger(private val annotation:IntegerArgument): Argument(annotation) {
    override fun getBrigadierArgument(): ArgumentType<*> {
        return IntegerArgumentType.integer(annotation.min,annotation.max)
    }

    override fun getType(): Class<*> {
        return Int::class.java
    }
}