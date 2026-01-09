package dev.slne.surf.trophy.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.trophy.core.service.trophyPlayerService
import dev.slne.surf.trophy.paper.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object PlayerConnectionListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        plugin.launch {
            val player = trophyPlayerService.loadOrGetOrCreatePlayerByUuidAndName(
                event.player.uniqueId,
                event.player.name
            )
            trophyPlayerService.cachePlayer(player)
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        trophyPlayerService.invalidatePlayer(event.player.uniqueId)
    }
}