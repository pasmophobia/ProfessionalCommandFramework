package net.propromp.pcf.arguments

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import net.propromp.pcf.api.annotationparser.AnnotationParser
import net.propromp.pcf.api.arguments.DoubleArgument

class DoubleAnnotationParser(val annotation:DoubleArgument): AnnotationParser() {
    override fun getBrigadierArgument(): ArgumentType<*> {
        return DoubleArgumentType.doubleArg(annotation.min,annotation.max)
    }

    override fun getType(): Class<*> {
        return Double::class.java
    }
}