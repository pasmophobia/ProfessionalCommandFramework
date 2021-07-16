package net.propromp.professionalcommandframework.api.arguments

/**
 * Double argument
 * parameter-type:Double(kotlin),double(java)
 *
 * @property min
 * @property max
 * @constructor Create empty Double argument
 */
annotation class DoubleArgument(val min:Double = Double.MIN_VALUE,val max:Double = Double.MAX_VALUE)