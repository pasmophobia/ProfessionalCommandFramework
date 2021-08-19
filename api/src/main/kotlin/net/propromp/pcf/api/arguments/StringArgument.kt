package net.propromp.pcf.api.arguments

/**
 * String argument
 * parameter-type:String
 *
 * @property type
 * @constructor Create empty String argument
 */
annotation class StringArgument(val type:StringType = StringType.SINGLE_WORD)
enum class StringType {
    SINGLE_WORD,GREEDY,SINGLE_STRING
}