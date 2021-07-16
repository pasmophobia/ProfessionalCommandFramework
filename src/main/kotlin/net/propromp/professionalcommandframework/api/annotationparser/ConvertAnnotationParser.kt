package net.propromp.professionalcommandframework.api.annotationparser

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext

/**
 * Convert argument
 * It is used to convert vanilla type to bukkit type
 *
 * @constructor Create empty Convert argument
 */
abstract class ConvertAnnotationParser(): AnnotationParser() {
    abstract val fromArgumentType:ArgumentType<*>
    abstract fun convert(any:Any,commandContext: CommandContext<*>):Any
    /**
     * Get brigadier argument type
     *
     * @return brigadier argument type
     */
    override fun getBrigadierArgument(): ArgumentType<*> {
        return fromArgumentType
    }
    /**
     * Get type of the method parameter(conversion destination type)
     *
     * @return
     */
    abstract override fun getType(): Class<*>

    /**
     * Get conversion source type
     *
     * @return
     */
    abstract fun getSourceType():Class<*>
}