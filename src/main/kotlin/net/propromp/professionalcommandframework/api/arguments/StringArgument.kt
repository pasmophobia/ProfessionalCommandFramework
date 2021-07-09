package net.propromp.professionalcommandframework.api.arguments

annotation class StringArgument(val type:StringArgumentType = StringArgumentType.SINGLE_WORD)
enum class StringArgumentType {
    SINGLE_WORD,GREEDY
}