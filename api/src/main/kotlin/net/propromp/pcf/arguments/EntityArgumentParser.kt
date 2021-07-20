package net.propromp.pcf.arguments

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import net.propromp.pcf.nms.NMS
import net.propromp.pcf.api.annotationparser.ConvertArgumentParser
import net.propromp.pcf.api.arguments.EntityArgument
import org.bukkit.entity.Player

class EntityArgumentParser(val annotation:EntityArgument) : ConvertArgumentParser(){
    override val fromArgumentType: ArgumentType<*>
     get() {

         return NMS("ArgumentEntity").getInstance(annotation.type.isSingle,annotation.type.isPlayer) as ArgumentType<*>
     }
    @Suppress
    override fun convert(entitySelector:Any, commandContext:CommandContext<*>): Any {
        entitySelector.javaClass.getMethod("c",NMS.getNMS("CommandListenerWrapper"))
        return if(annotation.type.isSingle){
            if(annotation.type.isPlayer){
                val nmsPlayer = NMS.fromClass(entitySelector.javaClass).invokeMethod(entitySelector,"c",commandContext.source)!!
                NMS.fromClass(NMS.getNMS("Entity")).invokeMethod(nmsPlayer,"getBukkitEntity")!!
            } else {
                val nmsEntity = NMS.fromClass(entitySelector.javaClass).invokeMethod(entitySelector,"a",commandContext.source)!!
                NMS.fromClass(NMS.getNMS("Entity")).invokeMethod(nmsEntity,"getBukkitEntity")!!
            }
        } else {
            if(annotation.type.isPlayer){
                val nmsPlayers = NMS.fromClass(entitySelector.javaClass).invokeMethod(entitySelector,"d",commandContext.source)!! as List<Any>
                nmsPlayers.map{ NMS.fromClass(NMS.getNMS("Entity")).invokeMethod(it,"getBukkitEntity")}
            } else {
                val nmsEntities = NMS.fromClass(entitySelector.javaClass).invokeMethod(entitySelector,"getEntities",commandContext.source)!! as List<Any>
                nmsEntities.map{ NMS.fromClass(NMS.getNMS("Entity")).invokeMethod(it,"getBukkitEntity")}
            }
        }
    }

    override fun getType(): Class<*> {
        return if(annotation.type.isSingle){
            if(annotation.type.isPlayer){
                Player::class.java
            } else {
                org.bukkit.entity.Entity::class.java
            }
        } else {
            List::class.java
        }
    }

    override fun getSourceType(): Class<*> {
        return NMS.getNMS("EntitySelector")
    }
}