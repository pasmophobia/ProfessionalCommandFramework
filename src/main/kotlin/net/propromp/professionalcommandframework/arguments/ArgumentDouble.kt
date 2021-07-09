package net.propromp.professionalcommandframework.arguments

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import net.propromp.professionalcommandframework.api.arguments.Argument
import net.propromp.professionalcommandframework.api.arguments.DoubleArgument

class ArgumentDouble(val annotation:DoubleArgument): Argument() {
    override fun getBrigadierArgument(): ArgumentType<*> {
        return DoubleArgumentType.doubleArg(annotation.min,annotation.max)
    }

    override fun getType(): Class<*> {
        return Double::class.java
    }
}