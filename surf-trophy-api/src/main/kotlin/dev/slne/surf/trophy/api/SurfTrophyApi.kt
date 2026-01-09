package dev.slne.surf.trophy.api

import dev.slne.surf.surfapi.core.api.util.requiredService
import org.bukkit.entity.Player

val surfTrophyApi = requiredService<SurfTrophyApi>()

interface SurfTrophyApi {
    fun showTrophyMenu(player: Player, viewer: Player)
}