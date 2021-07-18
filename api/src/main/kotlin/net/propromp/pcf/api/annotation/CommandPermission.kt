package net.propromp.pcf.api.annotation
@Target(AnnotationTarget.CLASS,AnnotationTarget.FUNCTION)
annotation class CommandPermission(val permission:String)