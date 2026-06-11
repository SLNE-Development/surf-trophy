package dev.slne.surf.trophy.paper.api

import com.google.auto.service.AutoService
import dev.slne.surf.api.paper.inventory.framework.open
import dev.slne.surf.trophy.api.SurfTrophyApi
import dev.slne.surf.trophy.api.trophy.ReceivedTrophy
import dev.slne.surf.trophy.core.common.service.PlayerTrophyService
import dev.slne.surf.trophy.paper.menu.noTrophiesMenu
import dev.slne.surf.trophy.paper.menu.otherTrophiesMenu
import dev.slne.surf.trophy.paper.menu.ownTrophiesMenu
import net.kyori.adventure.util.Services
import org.bukkit.Bukkit
import java.util.*

@AutoService(SurfTrophyApi::class)
class SurfTrophyApiImpl : SurfTrophyApi, Services.Fallback {
    override fun showTrophyMenu(target: UUID, viewer: UUID) {
        val viewerPlayer = Bukkit.getPlayer(viewer) ?: return
        val trophyPlayer = PlayerTrophyService.findPlayerByUuid(target) ?: return

        if (trophyPlayer.trophies.isEmpty()) {
            noTrophiesMenu().open(viewerPlayer, mapOf("target" to trophyPlayer))
            return
        }

        if (target == viewer) {
            ownTrophiesMenu().open(viewerPlayer, mapOf("player" to trophyPlayer))
        } else {
            otherTrophiesMenu().open(viewerPlayer, mapOf("player" to trophyPlayer))
        }
    }

    override suspend fun getOrLoadTrophies(targetUuid: UUID): List<ReceivedTrophy> =
        PlayerTrophyService.findPlayerByUuid(targetUuid)?.trophies ?: emptyList()
}