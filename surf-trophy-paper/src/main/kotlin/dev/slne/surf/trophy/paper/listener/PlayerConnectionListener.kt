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
        plugin.launch {
            val paperPlayer = event.player
            val player = PlayerTrophyService.loadOrGetOrCreatePlayerByUuidAndName(
                paperPlayer.uniqueId,
                paperPlayer.name
            )
            PlayerTrophyService.cachePlayer(player)
            val selectedTrophy = player.selectedTrophy

            if (selectedTrophy != null) {
                withContext(plugin.entityDispatcher(paperPlayer)) {
                    paperPlayer.inventory.setItemInOffHand(trophyItem(selectedTrophy))
                }
            }
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        PlayerTrophyService.invalidatePlayer(event.player.uniqueId)
    }
}
