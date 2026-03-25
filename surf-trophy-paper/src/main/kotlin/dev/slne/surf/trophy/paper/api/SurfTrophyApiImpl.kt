package dev.slne.surf.trophy.paper.api

import com.google.auto.service.AutoService
import dev.slne.surf.trophy.api.SurfTrophyApi
import dev.slne.surf.trophy.core.service.trophyPlayerService
import dev.slne.surf.trophy.paper.menu.otherTrophiesMenu
import dev.slne.surf.trophy.paper.menu.ownTrophiesMenu
import net.kyori.adventure.util.Services
import org.bukkit.Bukkit
import java.util.*

@AutoService(SurfTrophyApi::class)
class SurfTrophyApiImpl : SurfTrophyApi, Services.Fallback {
    override fun showTrophyMenu(target: UUID, viewer: UUID) {
        val viewerPlayer = Bukkit.getPlayer(viewer) ?: return
        val trophyPlayer = trophyPlayerService.findPlayerByUuid(target) ?: return

        if (target == viewer) {
            ownTrophiesMenu(trophyPlayer)
        } else {
            otherTrophiesMenu(trophyPlayer, viewerPlayer)
        }
    }
}