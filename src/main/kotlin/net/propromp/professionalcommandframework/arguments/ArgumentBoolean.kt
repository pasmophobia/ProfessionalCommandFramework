package net.propromp.professionalcommandframework.arguments

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import net.propromp.professionalcommandframework.api.arguments.Argument
import net.propromp.professionalcommandframework.api.arguments.BooleanArgument

class ArgumentBoolean(private val annotation:BooleanArgument):Argument() {
    override fun getBrigadierArgument(): ArgumentType<*> {
        return BoolArgumentType.bool()
    }

    override fun getType(): Class<*> {
        return Boolean::class.java
    }
}