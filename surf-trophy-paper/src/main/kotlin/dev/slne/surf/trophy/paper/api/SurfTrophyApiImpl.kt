package dev.slne.surf.trophy.paper.api

import com.google.auto.service.AutoService
import dev.slne.surf.trophy.api.SurfTrophyApi
import dev.slne.surf.trophy.core.service.trophyPlayerService
import dev.slne.surf.trophy.paper.menu.otherTrophiesMenu
import dev.slne.surf.trophy.paper.menu.ownTrophiesMenu
import net.kyori.adventure.util.Services
import org.bukkit.entity.Player

@AutoService(SurfTrophyApi::class)
class SurfTrophyApiImpl : SurfTrophyApi, Services.Fallback {
    override fun showTrophyMenu(player: Player, viewer: Player) {
        if (player.uniqueId == viewer.uniqueId) {
            val trophyPlayer = trophyPlayerService.findPlayerByUuid(player.uniqueId)
                ?: error("Trophy player not found")
            ownTrophiesMenu(trophyPlayer)
        } else {
            val trophyPlayer = trophyPlayerService.findPlayerByUuid(player.uniqueId)
                ?: error("Trophy player not found")
            otherTrophiesMenu(trophyPlayer, viewer)
        }
    }
}