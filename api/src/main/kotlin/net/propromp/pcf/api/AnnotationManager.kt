package net.propromp.pcf.api

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import net.propromp.pcf.api.annotation.*
import net.propromp.pcf.api.annotationparser.AnnotationParser
import net.propromp.pcf.api.annotationparser.ConvertAnnotationParser
import net.propromp.pcf.api.arguments.*
import net.propromp.pcf.api.exception.AnnotationParseException
import net.propromp.pcf.arguments.*
import net.propromp.pcf.arguments.EntityAnnotationParser
import net.propromp.pcf.nms.NMS
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.SimpleCommandMap
import java.lang.reflect.*

/**
 * Annotation manager
 *
 * @property manager
 * @constructor Create empty Annotation manager
 */
class AnnotationManager(val manager: CommandManager) {
    val argumentMap = mutableMapOf<Class<out Annotation>, Class<out AnnotationParser>>()
    init {
        registerArgument(BooleanArgument::class.java,BooleanAnnotationParser::class.java)
        registerArgument(DoubleArgument::class.java,DoubleAnnotationParser::class.java)
        registerArgument(FloatArgument::class.java,FloatAnnotationParser::class.java)
        registerArgument(IntegerArgument::class.java, IntegerAnnotationParser::class.java)
        registerArgument(LongArgument::class.java,LongAnnotationParser::class.java)
        registerArgument(StringArgument::class.java,StringAnnotationParser::class.java)
        registerArgument(EntityArgument::class.java,EntityAnnotationParser::class.java)
        registerArgument(ItemStackArgument::class.java,ItemStackAnnotationParser::class.java)
    }

    /**
     * Register argument
     *
     * @param annotationClass annotation class
     * @param annotationParserClass argument class
     */
    fun registerArgument(annotationClass: Class<out Annotation>, annotationParserClass: Class<out AnnotationParser>) {
        argumentMap[annotationClass] = annotationParserClass
    }

    /**
     * Register a command
     *
     * @param root class
     */
    fun register(root: Class<*>) {
        PcfCommand.fromClass(this,root).forEach {
            manager.register(it)
        }
    }
}