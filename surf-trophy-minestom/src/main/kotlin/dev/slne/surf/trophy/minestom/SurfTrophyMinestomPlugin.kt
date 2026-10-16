package dev.slne.surf.trophy.minestom

import dev.slne.surf.api.minestom.inventory.framework.register
import dev.slne.surf.api.minestom.plugin.SurfMinestomPlugin
import dev.slne.surf.trophy.core.client.TrophyClientInstance
import dev.slne.surf.trophy.core.common.service.TrophyService
import dev.slne.surf.trophy.minestom.command.trophyCommand
import dev.slne.surf.trophy.minestom.listener.ConnectionListener
import dev.slne.surf.trophy.minestom.menu.noTrophiesMenu
import dev.slne.surf.trophy.minestom.menu.otherTrophiesMenu
import dev.slne.surf.trophy.minestom.menu.ownTrophiesMenu

object SurfTrophyMinestomPlugin : SurfMinestomPlugin() {
    override suspend fun onLoad() {
        val instance = TrophyClientInstance.INSTANCE

        instance.onLoad()

        noTrophiesMenu.register()
        otherTrophiesMenu.register()
        ownTrophiesMenu.register()

        TrophyService.refreshTrophies()
    }

    override suspend fun onEnable() {
        TrophyClientInstance.INSTANCE.onEnable()
        trophyCommand()

        ConnectionListener.register(eventNode)
    }

    override suspend fun onDisable() {
        TrophyClientInstance.INSTANCE.onDisable()
    }
}
