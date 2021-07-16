package net.propromp.professionalcommandframework.api.arguments

/**
 * Entity argument
 * ## parameter type
 * |EntityArgumentType|parameter type|
 * |--|--|
 * |[net.propromp.professionalcommandframework.api.arguments.EntityArgumentType.SINGLE_PLAYER]|Player|
 * |[net.propromp.professionalcommandframework.api.arguments.EntityArgumentType.MANY_PLAYERS]|List<Player>|
 * |[net.propromp.professionalcommandframework.api.arguments.EntityArgumentType.SINGLE_ENTITY]|Entity|
 * |[net.propromp.professionalcommandframework.api.arguments.EntityArgumentType.MANY_ENTITIES]|List<Entity>|
 * @property type entity type.
 * @constructor Create empty Entity argument
 */
annotation class EntityArgument(val type:EntityArgumentType)
enum class EntityArgumentType(val isSingle:Boolean,val isPlayer:Boolean) {
    SINGLE_PLAYER(true,true),
    MANY_PLAYERS(false,true),
    SINGLE_ENTITY(true,false),
    MANY_ENTITIES(false,false)
}