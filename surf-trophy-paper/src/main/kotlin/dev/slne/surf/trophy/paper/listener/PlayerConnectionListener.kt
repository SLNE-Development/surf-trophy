package dev.slne.surf.trophy.paper.listener

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.trophy.core.common.service.PlayerTrophyService
import dev.slne.surf.trophy.paper.plugin
import dev.slne.surf.trophy.paper.util.trophyItem
import kotlinx.coroutines.withContext
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object PlayerConnectionListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val paperPlayer = event.player

        plugin.launch {
            val player = PlayerTrophyService.loadOrGetOrCreatePlayerByUuidAndName(
                paperPlayer.uniqueId,
                paperPlayer.name
            )

            if (!paperPlayer.isConnected) return@launch
            PlayerTrophyService.cachePlayer(player)
            if (!paperPlayer.isConnected) {
                PlayerTrophyService.invalidatePlayer(paperPlayer.uniqueId)
                return@launch
            }

            val selectedTrophy = player.selectedTrophy ?: return@launch

            withContext(plugin.entityDispatcher(paperPlayer)) {
                paperPlayer.inventory.setItemInOffHand(trophyItem(selectedTrophy))
            }
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        PlayerTrophyService.invalidatePlayer(event.player.uniqueId)
    }
}
