package net.propromp.professionalcommandframework.api.arguments

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.v1_16_R3.CommandListenerWrapper
import org.bukkit.command.CommandSender
import java.util.concurrent.CompletableFuture

abstract class CustomArgument<T>(annotation: Annotation):Argument(annotation) {
    override fun getBrigadierArgument(): ArgumentType<T> {
        return object:ArgumentType<T>{
            override fun parse(p0: StringReader): T {
                return this@CustomArgument.parse(p0.string)
            }

            override fun getExamples(): MutableCollection<String> {
                return this@CustomArgument.getExamples().toMutableList()
            }

            override fun <S : Any> listSuggestions(
                context: CommandContext<S>,
                builder: SuggestionsBuilder
            ): CompletableFuture<Suggestions> {
                this@CustomArgument.suggest((context.source as CommandListenerWrapper).bukkitSender).forEach {
                    builder.suggest(it)
                }
                return builder.buildFuture()
            }
        }
    }
    abstract fun parse(input:String):T//TODO exception
    abstract fun suggest(sender: CommandSender):List<String>
    abstract fun getExamples():List<String>
}