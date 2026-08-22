package dev.slne.surf.trophy.minestom.command.argument

import dev.slne.minestom.lobby.api.command.commandapi.CommandAPI
import dev.slne.minestom.lobby.api.command.commandapi.CommandAPICommand
import dev.slne.minestom.lobby.api.command.commandapi.CommandTree
import dev.slne.minestom.lobby.api.command.commandapi.argument.Argument
import dev.slne.minestom.lobby.api.command.commandapi.argument.CustomArgument
import dev.slne.minestom.lobby.api.command.commandapi.argument.TextArgument
import dev.slne.minestom.lobby.api.command.commandapi.suggestion.ArgumentSuggestions
import dev.slne.surf.trophy.api.trophy.Trophy
import dev.slne.surf.trophy.core.client.command.TrophyArgumentSupport
import dev.slne.surf.trophy.core.client.command.TrophyMessages

class TrophyArgument(nodeName: String) :
    CustomArgument<Trophy, String>(TextArgument(nodeName), { info ->
        val input = TrophyArgumentSupport.normalizeInput(info.currentInput)
        TrophyArgumentSupport.resolve(input)
            ?: CommandAPI.failWithMessage(TrophyMessages.unknownTrophy(input))
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection { TrophyArgumentSupport.suggestions() }
        )
    }
}

inline fun CommandTree.trophyArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    TrophyArgument(nodeName).setOptional(optional).apply(block)
)

inline fun Argument<*>.trophyArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    TrophyArgument(nodeName).setOptional(optional).apply(block)
)

inline fun CommandAPICommand.trophyArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand =
    withArguments(TrophyArgument(nodeName).setOptional(optional).apply(block))
