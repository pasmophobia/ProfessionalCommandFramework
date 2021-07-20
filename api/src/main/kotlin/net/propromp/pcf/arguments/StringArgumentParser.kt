package net.propromp.pcf.arguments

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import net.propromp.pcf.api.annotationparser.ArgumentParser
import net.propromp.pcf.api.arguments.StringArgument

class StringArgumentParser(private val annotation:StringArgument): ArgumentParser() {
    override fun getBrigadierArgument(): ArgumentType<*> {
        return when(annotation.type){
            net.propromp.pcf.api.arguments.StringArgumentType.SINGLE_WORD -> StringArgumentType.word()
            net.propromp.pcf.api.arguments.StringArgumentType.GREEDY -> StringArgumentType.greedyString()
        }
    }

    override fun getType(): Class<*> {
        return String::class.java
    }
}