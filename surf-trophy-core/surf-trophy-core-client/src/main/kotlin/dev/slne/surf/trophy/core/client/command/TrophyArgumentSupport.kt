package dev.slne.surf.trophy.core.client.command

import dev.slne.surf.trophy.api.trophy.Trophy
import dev.slne.surf.trophy.core.common.service.TrophyService

/**
 * The platform-neutral half of the trophy command argument: how raw input is read and which
 * values are suggested.
 */
object TrophyArgumentSupport {

    /**
     * Strips the quotes a suggested trophy name is wrapped in, so a name containing spaces
     * resolves to the same trophy whether or not the client kept the quotes.
     */
    fun normalizeInput(input: String): String = input.replace("\"", "")

    /**
     * Resolves the trophy named by [input], or `null` when no trophy carries that name.
     */
    fun resolve(input: String): Trophy? =
        TrophyService.findTrophyByName(normalizeInput(input))

    /**
     * Every known trophy name, quoted so that names containing spaces are completed as one
     * argument.
     */
    fun suggestions(): List<String> = TrophyService.getTrophies().map { "\"${it.name}\"" }
}
