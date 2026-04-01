package dev.slne.surf.trophy.paper.command.argument

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.TextArgument
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.trophy.api.trophy.Trophy
import dev.slne.surf.trophy.core.common.service.TrophyService

class TrophyArgument(nodeName: String) :
    CustomArgument<Trophy, String>(TextArgument(nodeName), { info ->
        val input = info.input.replace("\"", "")
        TrophyService.findTrophyByName(input)
            ?: throw CustomArgumentException.fromAdventureComponent(
                buildText {
                    appendErrorPrefix()
                    error("Die Trophäe '$input' wurde nicht gefunden.")
                })
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection {
                TrophyService.getTrophies().map { "\"${it.name}\"" }
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