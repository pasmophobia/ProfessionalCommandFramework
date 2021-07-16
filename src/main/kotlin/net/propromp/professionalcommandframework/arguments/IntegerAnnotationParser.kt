package net.propromp.professionalcommandframework.arguments

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import net.propromp.professionalcommandframework.api.annotationparser.AnnotationParser
import net.propromp.professionalcommandframework.api.arguments.IntegerArgument

class IntegerAnnotationParser(private val annotation:IntegerArgument): AnnotationParser() {
    override fun getBrigadierArgument(): ArgumentType<*> {
        return IntegerArgumentType.integer(annotation.min,annotation.max)
    }

    override fun getType(): Class<*> {
        return Int::class.java
    }
}