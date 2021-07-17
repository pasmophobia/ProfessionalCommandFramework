package net.propromp.pcf.arguments

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import net.propromp.pcf.api.annotationparser.AnnotationParser
import net.propromp.pcf.api.arguments.LongArgument

class LongAnnotationParser(private val annotation:LongArgument) : AnnotationParser(){
    override fun getBrigadierArgument(): ArgumentType<*> {
        return LongArgumentType.longArg(annotation.min,annotation.max)
    }

    override fun getType(): Class<*> {
        return Long::class.java
    }
}