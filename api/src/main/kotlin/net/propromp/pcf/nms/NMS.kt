package net.propromp.pcf.nms

import org.bukkit.Bukkit

class NMS(val clazz: Class<*>) {
    constructor(className: String):this(getNMS(className))
    companion object {
        val version = Bukkit.getServer().javaClass.getPackage().name.split(".")[3]
        fun getNMS(className: String):Class<out Any>{
            return Class.forName("net.minecraft.server.$version.$className")
        }
        fun getCraftBukkit(className: String):Class<out Any>{
            return Class.forName("org.bukkit.craftbukkit.$version.$className")
        }
        fun fromClass(clazz:Class<*>): NMS {
            return NMS(clazz)
        }
    }
    fun invokeMethod(obj:Any?,name:String,vararg params:Any):Any?{
        val method = clazz.getDeclaredMethod(name,*params.map {
            it::class.javaPrimitiveType ?: it::class.java
        }.toTypedArray())
        method.isAccessible=true
        return method.invoke(obj,*params)
    }
    fun getField(obj:Any?,name: String):Any?{
        val field = clazz.getDeclaredField(name)
        field.isAccessible=true
        return field.get(obj)
    }
    fun getInstance(vararg params:Any):Any?{
        val constructor = clazz.getDeclaredConstructor(*params.map {
            it::class.javaPrimitiveType ?: it::class.java
        }.toTypedArray())
        constructor.isAccessible=true
        return constructor.newInstance(*params)
    }
}