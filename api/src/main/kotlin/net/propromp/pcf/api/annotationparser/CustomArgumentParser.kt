package net.propromp.pcf.api.annotationparser

import com.google.gson.JsonObject
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandExceptionType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.propromp.pcf.api.exception.ArgumentParseException
import net.propromp.pcf.nms.NMS
import org.bukkit.command.CommandSender
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Custom annotation parser
 * ***YOU MUST IMPLEMENT A CONSTRUCTOR WITH ONE ANNOTATION PARAMETER
 *
 * @param T Type of the parameter
 * @constructor
 */
abstract class CustomArgumentParser<T>: ConvertArgumentParser() {
    /**
     * Get brigadier argument type
     *
     * @return brigadier argument type
     */
    override fun getBrigadierArgument(): ArgumentType<String> {
        return StringArgumentType.string()
    }

    abstract override fun getType(): Class<T>

    /**
     * Parse input to T
     * You can throw ArgumentParseException
     *
     * @param input input string
     * @return instance of T
     */
    @Throws(ArgumentParseException::class)
    abstract fun parse(input:String):T

    /**
     * suggestion
     *
     * @param sender command sender
     * @return list of suggestions
     */
    open fun suggest(sender: CommandSender):List<String> {
        return listOf()
    }
    /**
     * Get examples
     *
     * @return list of examples
     */
    open fun getExamples():Collection<String> {
        return listOf()
    }

    override val fromArgumentType: ArgumentType<*> = StringArgumentType.string()
    override fun convert(any: Any, commandContext: CommandContext<*>): Any {
        try {
            return parse(any as String) as Any
        } catch(e:ArgumentParseException) {
            throw CommandSyntaxException(object : CommandExceptionType {}, { e.message }, any as String, 0)
        }
    }

    override fun getSourceType(): Class<*> {
        return String::class.java
    }

    internal val suggestionProvider = SuggestionProvider<Any> { context, builder ->
        suggest(NMS("CommandListenerWrapper").invokeMethod(context.source,"getBukkitSender") as CommandSender).forEach {
            builder.suggest(it)
        }
        builder.buildFuture()
    }
}