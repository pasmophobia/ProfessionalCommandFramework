package net.propromp.pcf.api.annotationparser

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.propromp.pcf.nms.NMS
import org.bukkit.command.CommandSender
import java.util.concurrent.CompletableFuture

/**
 * Custom annotation parser
 * ***YOU MUST IMPLEMENT A CONSTRUCTOR WITH ONE ANNOTATION PARAMETER
 *
 * @param T Type of the parameter
 * @constructor
 */
abstract class CustomAnnotationParser<T>: AnnotationParser() {
    /**
     * Get brigadier argument type
     *
     * @return brigadier argument type
     */
    override fun getBrigadierArgument(): ArgumentType<T> {
        return object:ArgumentType<T>{
            override fun parse(p0: StringReader): T {
                return this@CustomAnnotationParser.parse(p0.string)
            }

            override fun getExamples(): MutableCollection<String> {
                return this@CustomAnnotationParser.getExamples().toMutableList()
            }

            override fun <S : Any> listSuggestions(
                context: CommandContext<S>,
                builder: SuggestionsBuilder
            ): CompletableFuture<Suggestions> {
                this@CustomAnnotationParser.suggest(NMS("CommandListenerWrapper").invokeMethod(context.source,"getBukkitSender") as CommandSender).forEach {
                    builder.suggest(it)
                }
                return builder.buildFuture()
            }
        }
    }

    /**
     * Parse input to T
     *
     * @param input input string
     * @return instance of T
     */
    abstract fun parse(input:String):T//TODO exception

    /**
     * suggestion
     *
     * @param sender command sender
     * @return list of suggestions
     */
    fun suggest(sender: CommandSender):List<String> {
        return listOf()
    }

    /**
     * Get examples
     *
     * @return list of examples
     */
    fun getExamples():List<String> {
        return listOf()
    }
}