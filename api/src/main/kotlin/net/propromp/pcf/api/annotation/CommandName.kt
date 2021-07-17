package net.propromp.pcf.api.annotation

/**
 * name of the root command or the sub command
 *
 * @property name name of the command
 */
@Target(AnnotationTarget.CLASS,AnnotationTarget.FUNCTION)
annotation class CommandName(val name:String)
