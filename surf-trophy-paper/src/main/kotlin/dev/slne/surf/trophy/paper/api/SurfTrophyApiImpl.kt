package dev.slne.surf.trophy.paper.api

import com.google.auto.service.AutoService
import dev.slne.surf.api.paper.inventory.framework.open
import dev.slne.surf.trophy.api.SurfTrophyApi
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
            noTrophiesMenu(trophyPlayer, viewerPlayer).open(viewerPlayer)
            return
        }

        if (target == viewer) {
            ownTrophiesMenu(trophyPlayer).open(viewerPlayer)
        } else {
            otherTrophiesMenu(trophyPlayer).open(viewerPlayer)
        }
    }
}