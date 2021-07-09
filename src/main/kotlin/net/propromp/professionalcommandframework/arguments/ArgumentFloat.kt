package net.propromp.professionalcommandframework.arguments

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import net.propromp.professionalcommandframework.api.arguments.Argument
import net.propromp.professionalcommandframework.api.arguments.FloatArgument

class ArgumentFloat(private val annotation:FloatArgument): Argument() {
    override fun getBrigadierArgument(): ArgumentType<*> {
        return FloatArgumentType.floatArg(annotation.min,annotation.max)
    }

    override fun getType(): Class<*> {
        return Float::class.java
    }
}