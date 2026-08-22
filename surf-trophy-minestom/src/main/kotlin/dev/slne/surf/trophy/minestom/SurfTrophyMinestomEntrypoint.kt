package dev.slne.surf.trophy.minestom

import com.google.inject.Inject
import com.google.inject.Singleton
import dev.slne.minestom.lobby.api.plugin.MinestomPluginEntrypoint
import dev.slne.minestom.lobby.api.plugin.annotation.DataDirectory
import dev.slne.surf.api.minestom.inventory.framework.register
import dev.slne.surf.trophy.core.client.TrophyClientInstance
import dev.slne.surf.trophy.core.common.service.TrophyService
import dev.slne.surf.trophy.minestom.menu.noTrophiesMenu
import dev.slne.surf.trophy.minestom.menu.otherTrophiesMenu
import dev.slne.surf.trophy.minestom.menu.ownTrophiesMenu
import java.nio.file.Path

@Singleton
class SurfTrophyMinestomEntrypoint @Inject constructor(
    @DataDirectory path: Path
) : MinestomPluginEntrypoint {

    init {
        dataPath = path
    }

    override suspend fun start() {
        val instance = TrophyClientInstance.INSTANCE

        instance.onLoad()

        noTrophiesMenu.register()
        otherTrophiesMenu.register()
        ownTrophiesMenu.register()

        instance.onEnable()

        TrophyService.refreshTrophies()
    }

    override suspend fun stop() {
        TrophyClientInstance.INSTANCE.onDisable()
    }

    companion object {
        lateinit var dataPath: Path
            private set
    }
}
