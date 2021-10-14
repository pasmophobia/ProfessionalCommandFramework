package net.propromp.pcf.api

import net.propromp.pcf.api.annotationparser.ArgumentParser
import net.propromp.pcf.api.arguments.*
import net.propromp.pcf.arguments.*
import net.propromp.pcf.arguments.EntityArgumentParser

/**
 * Annotation manager
 *
 * @property manager
 * @constructor Create empty Annotation manager
 */
class AnnotationManager(val manager: CommandManager) {
    val argumentMap = mutableMapOf<Class<out Annotation>, Class<out ArgumentParser>>()
    init {
        registerArgument(BooleanArgument::class.java,BooleanArgumentParser::class.java)
        registerArgument(DoubleArgument::class.java,DoubleArgumentParser::class.java)
        registerArgument(FloatArgument::class.java,FloatArgumentParser::class.java)
        registerArgument(IntegerArgument::class.java, IntegerArgumentParser::class.java)
        registerArgument(LongArgument::class.java,LongArgumentParser::class.java)
        registerArgument(StringArgument::class.java,StringArgumentParser::class.java)
        registerArgument(EntityArgument::class.java,EntityArgumentParser::class.java)
        registerArgument(ItemStackArgument::class.java,ItemStackArgumentParser::class.java)
        registerArgument(LocationArgument::class.java,LocationArgumentParser::class.java)
    }

    /**
     * Register argument
     *
     * @param annotationClass annotation class
     * @param annotationParserClass argument class
     */
    fun registerArgument(annotationClass: Class<out Annotation>, annotationParserClass: Class<out ArgumentParser>) {
        argumentMap[annotationClass] = annotationParserClass
    }

    /**
     * Register a command
     *
     * @param root root command instance
     */
    fun register(root: Any) {
        manager.register(PcfCommand.fromClass(this,root))
    }
}