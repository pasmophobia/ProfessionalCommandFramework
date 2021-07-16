package net.propromp.professionalcommandframework.api.arguments

/**
 * String argument
 * parameter-type:String
 *
 * @property type
 * @constructor Create empty String argument
 */
annotation class StringArgument(val type:StringArgumentType = StringArgumentType.SINGLE_WORD)
enum class StringArgumentType {
    SINGLE_WORD,GREEDY
}