package net.propromp.pcf.arguments

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import net.propromp.pcf.api.annotationparser.ConvertArgumentParser
import net.propromp.pcf.api.arguments.ItemStackArgument
import net.propromp.pcf.nms.NMS
import org.bukkit.inventory.ItemStack

class ItemStackArgumentParser(val annotation:ItemStackArgument): ConvertArgumentParser() {
    override val fromArgumentType: ArgumentType<*> = NMS("ArgumentItemStack").getInstance() as ArgumentType<*>

    override fun convert(predicate:Any, commandContext: CommandContext<*>): ItemStack {
        val nmsStack = NMS("ArgumentPredicateItemStack").invokeMethod(predicate,"a",1,false)!!
        return NMS("ItemStack").invokeMethod(nmsStack,"getBukkitStack") as ItemStack
    }

    override fun getType(): Class<*> {
        return ItemStack::class.java
    }

    override fun getSourceType(): Class<*> {
        return NMS.getNMS("ArgumentPredicateItemStack")

    }
}