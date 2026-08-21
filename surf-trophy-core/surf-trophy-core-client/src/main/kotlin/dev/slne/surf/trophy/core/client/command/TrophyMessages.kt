package dev.slne.surf.trophy.core.client.command

import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.pagination.Pagination
import dev.slne.surf.trophy.api.trophy.Trophy
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration

/**
 * The player-facing messages the trophy commands send, shared by every platform.
 */
object TrophyMessages {

    val dataNotLoaded: Component = buildText {
        appendErrorPrefix()
        error("Deine Daten konnten nicht geladen werden.")
    }

    val playerNotFound: Component = buildText {
        appendErrorPrefix()
        error("Der Spieler wurde nicht gefunden.")
    }

    val trophiesRefreshed: Component = buildText {
        appendSuccessPrefix()
        success("Alle Trophäen wurden neu geladen.")
    }

    val trophyCreated: Component = buildText {
        appendSuccessPrefix()
        success("Die Trophäe wurde erstellt.")
    }

    val noItemInHand: Component = buildText {
        appendErrorPrefix()
        error("Du musst ein Item in der Hand halten, um eine Trophäe zu erstellen.")
    }

    val trophyDeleted: Component = buildText {
        appendSuccessPrefix()
        success("Die Trophäe wurde gelöscht.")
    }

    val trophyNotFound: Component = buildText {
        appendErrorPrefix()
        error("Die Trophäe wurde nicht gefunden.")
    }

    val trophyGiven: Component = buildText {
        appendSuccessPrefix()
        success("Die Trophäe wurde dem Spieler gegeben.")
    }

    val trophyAlreadyOwned: Component = buildText {
        appendErrorPrefix()
        error("Der Spieler besitzt die Trophäe bereits.")
    }

    val trophyTaken: Component = buildText {
        appendSuccessPrefix()
        success("Die Trophäe wurde dem Spieler genommen.")
    }

    val trophyNotOwned: Component = buildText {
        appendErrorPrefix()
        error("Der Spieler besitzt die Trophäe nicht.")
    }

    /**
     * The failure a trophy argument reports for an unknown [input].
     */
    fun unknownTrophy(input: String): Component = buildText {
        appendErrorPrefix()
        error("Die Trophäe '$input' wurde nicht gefunden.")
    }

    /**
     * Renders [trophies] as a paginated list, each entry revealing its description on hover.
     */
    fun trophyList(trophies: Collection<Trophy>): Component = buildText {
        appendNewline()
        append(trophyListPagination.renderComponent(trophies))
    }

    private val trophyListPagination by lazy {
        Pagination<Trophy> {
            title { primary("Trophäen".toSmallCaps(), TextDecoration.BOLD) }
            rowRenderer { trophy, _ ->
                listOf(buildText {
                    spacer("- ")
                    variableValue(trophy.name)

                    hoverEvent(buildText {
                        variableValue(trophy.description)
                    })
                })
            }
        }
    }
}
