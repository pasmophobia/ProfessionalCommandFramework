package net.propromp.pcf.api.annotation

@Target(AnnotationTarget.FUNCTION,AnnotationTarget.CLASS)
annotation class CommandAlias(val name:Array<String>)
