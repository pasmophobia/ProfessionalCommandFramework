package net.propromp.pcf.arguments

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import net.propromp.pcf.api.annotationparser.AnnotationParser
import net.propromp.pcf.api.arguments.BooleanArgument

class BooleanAnnotationParser(private val annotation:BooleanArgument): AnnotationParser() {
    override fun getBrigadierArgument(): ArgumentType<*> {
        return BoolArgumentType.bool()
    }

    override fun getType(): Class<*> {
        return Boolean::class.java
    }
}