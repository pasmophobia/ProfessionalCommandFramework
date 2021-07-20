package net.propromp.pcf.example

import net.propromp.pcf.api.annotationparser.CustomArgumentParser
import net.propromp.pcf.api.exception.ArgumentParseException
import org.bukkit.command.CommandSender
import java.lang.Exception

/**
 * This is an example of CustomArgumentParser
 *
 * @constructor Create empty Food argument parser
 */
class FoodArgumentParser(annotation:FoodArgument): CustomArgumentParser<Food>() {
    override fun getType(): Class<Food> {
        return Food::class.java
    }

    /**
     * Parse input to T
     * You can throw ArgumentParseException
     *
     * @param input input string
     * @return instance of T
     */
    override fun parse(input: String): Food {
        try {
            return Food.valueOf(input.uppercase())
        } catch(e:Exception){
            throw ArgumentParseException("That's not food!")
        }
    }
    override fun suggest(sender:CommandSender): List<String> {
        return Food.values().map{it.name.lowercase()}
    }

    override fun getExamples(): List<String> {
        return Food.values().map{it.name.lowercase()}
    }
}

/**
 * annotation
 *
 * @constructor Create empty Food argument
 */
annotation class FoodArgument

/**
 * I ❤ those foods
 */
enum class Food {
    PIZZA,SUSHI,YAKINIKU
}