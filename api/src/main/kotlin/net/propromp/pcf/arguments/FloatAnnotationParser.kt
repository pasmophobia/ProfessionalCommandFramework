package net.propromp.pcf.arguments

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import net.propromp.pcf.api.annotationparser.AnnotationParser
import net.propromp.pcf.api.arguments.FloatArgument

class FloatAnnotationParser(private val annotation:FloatArgument): AnnotationParser() {
    override fun getBrigadierArgument(): ArgumentType<*> {
        return FloatArgumentType.floatArg(annotation.min,annotation.max)
    }

    override fun getType(): Class<*> {
        return Float::class.java
    }
}