package net.propromp.pcf.api.arguments

/**
 * Integer argument
 * parameter-type:Int(kotlin),int(java)
 *
 * @property min
 * @property max
 * @constructor Create empty Integer argument
 */
annotation class IntegerArgument(val min:Int = Int.MIN_VALUE,val max:Int = Int.MAX_VALUE)
