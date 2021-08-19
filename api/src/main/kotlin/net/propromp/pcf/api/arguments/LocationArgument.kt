package net.propromp.pcf.api.arguments

annotation class LocationArgument(val type:LocationArgumentType = LocationArgumentType.DECIMAL_LOCATION)
enum class LocationArgumentType {
    INTEGER_LOCATION,DECIMAL_LOCATION
}