package net.propromp.professionalcommandframework.api.arguments

/**
 * Float argument
 * parameter-type:Float(kotlin),float(java)
 *
 * @property min
 * @property max
 * @constructor Create empty Float argument
 */
annotation class FloatArgument(val min:Float = Float.MIN_VALUE,val max:Float = Float.MAX_VALUE)
