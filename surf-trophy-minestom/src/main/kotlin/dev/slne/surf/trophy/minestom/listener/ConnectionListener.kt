package dev.slne.surf.trophy.minestom.listener

import dev.slne.surf.api.minestom.coroutine.minestomAsyncScope
import dev.slne.surf.api.minestom.coroutine.withEntity
import dev.slne.surf.api.minestom.event.EventRegistrar
import dev.slne.surf.api.minestom.extension.addListener
import dev.slne.surf.trophy.core.common.service.PlayerTrophyService
import dev.slne.surf.trophy.minestom.util.trophyItem
import kotlinx.coroutines.launch
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerSpawnEvent

object ConnectionListener : EventRegistrar {
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
