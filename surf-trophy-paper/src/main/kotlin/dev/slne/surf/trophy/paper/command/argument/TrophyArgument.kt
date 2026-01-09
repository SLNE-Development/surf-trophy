package dev.slne.surf.trophy.paper.command.argument

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.TextArgument
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.trophy.api.trophy.Trophy
import dev.slne.surf.trophy.core.service.trophyService

class TrophyArgument(nodeName: String) :
    CustomArgument<Trophy, String>(TextArgument(nodeName), { info ->
        trophyService.findTrophyByName(info.input)
            ?: throw CustomArgumentException.fromAdventureComponent(
                buildText {
                    appendPrefix()
                    error("Die Trophäe wurde nicht gefunden.")
                })
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection {
                trophyService.getTrophies().map { "\"${it.name}\"" }
            }
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