package dev.slne.surf.trophy.minestom.command

import com.google.inject.Inject
import dev.slne.minestom.lobby.api.command.CommandRegistrar

/**
 * Registers the trophy commands of this plugin.
 */
class TrophyCommandRegistrar @Inject constructor() : CommandRegistrar {
    override fun register() {
        trophyCommand()
    }
}
