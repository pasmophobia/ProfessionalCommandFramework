package net.propromp.professionalcommandframework.api.arguments

import com.mojang.brigadier.arguments.ArgumentType

/**
 * Argument
 * ***You must implement a constructor with annotation parameter
 *
 * @constructor
 *
 * @param annotation arugument annotation
 */
abstract class Argument {
    /**
     * Get brigadier argument type
     *
     * @return brigadier argument type
     */
    abstract fun getBrigadierArgument():ArgumentType<*>

    /**
     * Get type of the method parameter
     *
     * @return
     */
    abstract fun getType():Class<*>
}