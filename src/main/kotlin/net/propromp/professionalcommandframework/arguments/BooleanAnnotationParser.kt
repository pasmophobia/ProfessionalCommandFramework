package net.propromp.professionalcommandframework.arguments

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import net.propromp.professionalcommandframework.api.annotationparser.AnnotationParser
import net.propromp.professionalcommandframework.api.arguments.BooleanArgument

class BooleanAnnotationParser(private val annotation:BooleanArgument): AnnotationParser() {
    override fun getBrigadierArgument(): ArgumentType<*> {
        return BoolArgumentType.bool()
    }

    override fun getType(): Class<*> {
        return Boolean::class.java
    }
}