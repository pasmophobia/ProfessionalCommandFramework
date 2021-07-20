package net.propromp.pcf.arguments

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import net.propromp.pcf.api.annotationparser.ArgumentParser
import net.propromp.pcf.api.arguments.FloatArgument

class FloatArgumentParser(private val annotation:FloatArgument): ArgumentParser() {
    override fun getBrigadierArgument(): ArgumentType<*> {
        return FloatArgumentType.floatArg(annotation.min,annotation.max)
    }

    override fun getType(): Class<*> {
        return Float::class.java
    }
}