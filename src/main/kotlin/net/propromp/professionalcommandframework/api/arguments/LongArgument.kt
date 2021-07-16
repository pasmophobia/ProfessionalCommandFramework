package net.propromp.professionalcommandframework.api.arguments

/**
 * Long argument
 * parameter-type:Long(kotlin),long(java)
 *
 * @property min
 * @property max
 * @constructor Create empty Long argument
 */
annotation class LongArgument(val min:Long = Long.MIN_VALUE,val max:Long = Long.MAX_VALUE)
