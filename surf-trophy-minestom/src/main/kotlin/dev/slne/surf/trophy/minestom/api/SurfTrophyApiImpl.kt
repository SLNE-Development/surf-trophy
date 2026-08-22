package dev.slne.surf.trophy.minestom.api

import com.google.auto.service.AutoService
import dev.slne.minestom.lobby.api.extension.ConnectionManager
import dev.slne.surf.api.minestom.inventory.framework.open
import dev.slne.surf.trophy.api.SurfTrophyApi
import dev.slne.surf.trophy.api.trophy.ReceivedTrophy
import dev.slne.surf.trophy.core.common.service.PlayerTrophyService
import dev.slne.surf.trophy.minestom.menu.noTrophiesMenu
import dev.slne.surf.trophy.minestom.menu.otherTrophiesMenu
import dev.slne.surf.trophy.minestom.menu.ownTrophiesMenu
import java.util.*

@AutoService(SurfTrophyApi::class)
class SurfTrophyApiImpl : SurfTrophyApi {
    override fun showTrophyMenu(target: UUID, viewer: UUID) {
        val viewerPlayer = ConnectionManager.getOnlinePlayerByUuid(viewer) ?: return
        val trophyPlayer = PlayerTrophyService.findPlayerByUuid(target) ?: return

        if (trophyPlayer.trophies.isEmpty()) {
            noTrophiesMenu.open(viewerPlayer, mapOf("target" to trophyPlayer))
            return
        }

        if (target == viewer) {
            ownTrophiesMenu.open(viewerPlayer, mapOf("player" to trophyPlayer))
        } else {
            otherTrophiesMenu.open(viewerPlayer, mapOf("player" to trophyPlayer))
        }
    }

    override suspend fun getOrLoadTrophies(targetUuid: UUID): List<ReceivedTrophy> =
        PlayerTrophyService.loadOrGetPlayerByUuid(targetUuid)?.trophies ?: emptyList()
}
