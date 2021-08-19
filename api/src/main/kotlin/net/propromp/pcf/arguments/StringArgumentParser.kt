package net.propromp.pcf.arguments

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import net.propromp.pcf.api.annotationparser.ArgumentParser
import net.propromp.pcf.api.arguments.StringArgument
import net.propromp.pcf.api.arguments.StringType.*

class StringArgumentParser(private val annotation:StringArgument): ArgumentParser() {
    override fun getBrigadierArgument(): ArgumentType<*> {
        return when(annotation.type){
            SINGLE_WORD -> StringArgumentType.word()
            GREEDY -> StringArgumentType.greedyString()
            SINGLE_STRING -> StringArgumentType.string()
        }
    }

    override fun getType(): Class<*> {
        return String::class.java
    }
}