package dev.slne.surf.trophy.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.util.dateTimeFormatter
import dev.slne.surf.trophy.core.common.service.trophyPlayerService
import dev.slne.surf.trophy.core.paper.util.item
import dev.slne.surf.trophy.paper.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

object PlayerConnectionListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        plugin.launch {
            val player = trophyPlayerService.loadOrGetOrCreatePlayerByUuidAndName(
                event.player.uniqueId,
                event.player.name
            )
            trophyPlayerService.cachePlayer(player)

            player.selectedTrophy?.let {
                event.player.inventory.setItemInOffHand(it.trophy.item.apply {
                    displayName {
                        variableValue(it.trophy.name)
                    }

                    buildLore {
                        emptyLine()
                        line {
                            variableValue("Beschreibung:".toSmallCaps())
                        }

                        line {
                            note(it.trophy.description)
                        }

                        emptyLine()

                        line {
                            variableValue("Erhalten am:".toSmallCaps())
                        }

                        line {
                            note(
                                dateTimeFormatter.format(
                                    ZonedDateTime.ofInstant(
                                        Instant.ofEpochMilli(it.receivedAt),
                                        ZoneId.of("Europe/Berlin")
                                    )
                                )
                            )
                        }
                    }
                })
            }
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        trophyPlayerService.invalidatePlayer(event.player.uniqueId)
    }
}