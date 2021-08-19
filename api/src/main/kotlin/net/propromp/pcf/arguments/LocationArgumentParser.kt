package net.propromp.pcf.arguments

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import net.propromp.pcf.api.annotationparser.ConvertArgumentParser
import net.propromp.pcf.api.arguments.LocationArgument
import net.propromp.pcf.api.arguments.LocationArgumentType
import net.propromp.pcf.nms.NMS
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.inventory.ItemStack

class LocationArgumentParser(val annotation:LocationArgument):ConvertArgumentParser() {
    override val fromArgumentType: ArgumentType<*>
        get() {
            return when(annotation.type){
                LocationArgumentType.INTEGER_LOCATION -> NMS("ArgumentPosition").getInstance() as ArgumentType<*>
                LocationArgumentType.DECIMAL_LOCATION -> NMS("ArgumentVec3").getInstance(true) as ArgumentType<*>
            }
        }

    override fun convert(any:Any, commandContext: CommandContext<*>): Location {
        val nmsBlockPosition = NMS("IVectorPosition").invokeMethod(any,"c",commandContext.source)!!
        val x = (NMS("BaseBlockPosition").invokeMethod(nmsBlockPosition,"getX") as Int).toDouble()
        val y = (NMS("BaseBlockPosition").invokeMethod(nmsBlockPosition,"getY") as Int).toDouble()
        val z = (NMS("BaseBlockPosition").invokeMethod(nmsBlockPosition,"getZ") as Int).toDouble()
        val nmsWorld = NMS("CommandListenerWrapper").invokeMethod(commandContext.source,"getWorld")
        return Location(NMS("World").invokeMethod(nmsWorld,"getWorld") as World,x,y,z)
    }

    override fun getType(): Class<*> {
        return Location::class.java
    }

    override fun getSourceType(): Class<*> {
        return NMS.getNMS("IVectorPosition")
    }
}