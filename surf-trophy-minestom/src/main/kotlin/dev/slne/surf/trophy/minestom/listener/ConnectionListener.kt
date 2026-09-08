package dev.slne.surf.trophy.minestom.listener

import com.google.inject.Inject
import dev.slne.minestom.lobby.api.coroutine.minestomAsyncScope
import dev.slne.minestom.lobby.api.coroutine.withEntity
import dev.slne.minestom.lobby.api.event.EventRegistrar
import dev.slne.minestom.lobby.api.extension.addListener
import dev.slne.surf.trophy.core.common.service.PlayerTrophyService
import dev.slne.surf.trophy.minestom.util.trophyItem
import kotlinx.coroutines.launch
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerSpawnEvent

class ConnectionListener @Inject constructor() : EventRegistrar {
    override fun register(node: EventNode<Event>) {
        node.addListener<PlayerSpawnEvent> { event ->
            if (!event.isFirstSpawn) return@addListener

            val player = event.player

            minestomAsyncScope.launch {
                val trophyPlayer = PlayerTrophyService.loadOrGetOrCreatePlayerByUuidAndName(
                    player.uuid,
                    player.username
                )

                if (!player.isOnline) return@launch
                PlayerTrophyService.cachePlayer(trophyPlayer)
                if (!player.isOnline) {
                    PlayerTrophyService.invalidatePlayer(player.uuid)
                    return@launch
                }

                val selectedTrophy = trophyPlayer.selectedTrophy ?: return@launch

                val item = trophyItem(selectedTrophy)
                player.withEntity { it.itemInOffHand = item }
            }
        }

        node.addListener<PlayerDisconnectEvent> { event ->
            PlayerTrophyService.invalidatePlayer(event.player.uuid)
        }
    }
}
