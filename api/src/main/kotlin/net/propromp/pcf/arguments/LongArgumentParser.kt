package net.propromp.pcf.arguments

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import net.propromp.pcf.api.annotationparser.ArgumentParser
import net.propromp.pcf.api.arguments.LongArgument

class LongArgumentParser(private val annotation:LongArgument) : ArgumentParser(){
    override fun getBrigadierArgument(): ArgumentType<*> {
        return LongArgumentType.longArg(annotation.min,annotation.max)
    }

    override fun getType(): Class<*> {
        return Long::class.java
    }
}