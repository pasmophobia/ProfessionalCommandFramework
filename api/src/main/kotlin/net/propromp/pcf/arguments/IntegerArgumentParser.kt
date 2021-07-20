package net.propromp.pcf.arguments

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import net.propromp.pcf.api.annotationparser.ArgumentParser
import net.propromp.pcf.api.arguments.IntegerArgument

class IntegerArgumentParser(private val annotation:IntegerArgument): ArgumentParser() {
    override fun getBrigadierArgument(): ArgumentType<*> {
        return IntegerArgumentType.integer(annotation.min,annotation.max)
    }

    override fun getType(): Class<*> {
        return Int::class.java
    }
}