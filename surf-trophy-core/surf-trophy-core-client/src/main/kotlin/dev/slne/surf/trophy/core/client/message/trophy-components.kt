package dev.slne.surf.trophy.core.client.message

import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.util.dateTimeFormatter
import dev.slne.surf.api.core.util.mutableObjectListOf
import dev.slne.surf.trophy.api.trophy.ReceivedTrophy
import dev.slne.surf.trophy.api.trophy.Trophy
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.text.format.TextDecoration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

private val trophyTimeZone: ZoneId = ZoneId.of("Europe/Berlin")

/**
 * Formats the instant a trophy was received in the network's display time zone.
 *
 * @param epochMillis the instant in milliseconds since the epoch
 */
fun formatReceivedAt(epochMillis: Long): String = dateTimeFormatter.format(
    ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), trophyTimeZone)
)

/** The name a trophy is displayed under. */
fun trophyDisplayName(trophy: Trophy): Component = buildText {
    variableValue(trophy.name)
}

/** The name the currently selected trophy is displayed under. */
val selectedTrophyDisplayName: Component = buildText {
    variableValue("Ausgewählt".toSmallCaps())
}

/**
 * The lore describing a received trophy: its description and when it was received.
 */
fun receivedTrophyLore(
    receivedTrophy: ReceivedTrophy
): List<Component> = mutableObjectListOf<Component>(6).apply {
    add(empty())
    add(buildText { variableValue("Beschreibung:".toSmallCaps()) })
    add(buildText { note(receivedTrophy.trophy.description) })

    add(empty())
    add(buildText { variableValue("Erhalten am:".toSmallCaps()) })
    add(buildText { note(formatReceivedAt(receivedTrophy.receivedAt)) })
}

/**
 * The headline shown when the inspected player owns no trophies.
 *
 * @param self whether the viewer is inspecting their own trophies
 * @param targetName the name of the inspected player
 */
fun noTrophiesDisplayName(self: Boolean, targetName: String): Component = buildText {
    if (self) {
        error(
            "Du hast noch keine Trophäen erhalten!".toSmallCaps(),
            TextDecoration.BOLD
        )
    } else {
        error(
            "$targetName hat noch keine Trophäen erhalten!".toSmallCaps(),
            TextDecoration.BOLD
        )
    }
}

/** The explanation shown alongside [noTrophiesDisplayName]. */
val noTrophiesLore: Array<Component> = arrayOf(
    empty(),
    buildText { spacer("» "); info("Trophäen sind Zeichen deiner Geschichte auf diesem Server.") },
    buildText { spacer("» "); info("Du erhältst sie durch besondere Leistungen –") },
    buildText { spacer("» "); info("etwa durch Events oder Abenteuer.") },

    empty(),
    buildText { spacer("» "); info("Einige Trophäen sind streng limitiert.") },
    buildText { spacer("» "); info("Manche sind exklusiv für besondere Spieler.") },

    empty(),
    buildText { spacer("» "); info("Jede Trophäe erzählt eine Geschichte.") },
)
