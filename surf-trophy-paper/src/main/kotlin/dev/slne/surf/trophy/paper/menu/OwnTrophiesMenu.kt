package dev.slne.surf.trophy.paper.menu

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.util.dateTimeFormatter
import dev.slne.surf.api.paper.builder.buildLore
import dev.slne.surf.api.paper.builder.displayName
import dev.slne.surf.api.paper.inventory.framework.dsl.onItemClick
import dev.slne.surf.api.paper.inventory.framework.view.layoutTarget
import dev.slne.surf.api.paper.inventory.framework.view.onClose
import dev.slne.surf.api.paper.inventory.framework.view.paginatedSurfView
import dev.slne.surf.api.paper.inventory.framework.view.pagination.pagination
import dev.slne.surf.api.paper.inventory.framework.view.settings
import dev.slne.surf.api.paper.inventory.framework.view.settings.PaginationViewRows
import dev.slne.surf.trophy.api.player.TrophyPlayer
import dev.slne.surf.trophy.core.common.service.PlayerTrophyService
import dev.slne.surf.trophy.core.paper.util.item
import dev.slne.surf.trophy.paper.plugin
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

fun ownTrophiesMenu(player: TrophyPlayer) = paginatedSurfView("Deine Trophäen") {
    pagination {
        lazySource { player.trophies }

        elementFactory { _, builder, _, trophy ->
            builder.withItem(trophy.trophy.item.clone().apply {
                displayName { variableValue(trophy.trophy.name) }

                buildLore {
                    emptyLine()
                    line { variableValue("Beschreibung:".toSmallCaps()) }
                    line { note(trophy.trophy.description) }

                    emptyLine()
                    line { variableValue("Erhalten am:".toSmallCaps()) }
                    line {
                        note(
                            dateTimeFormatter.format(
                                ZonedDateTime.ofInstant(
                                    Instant.ofEpochMilli(trophy.receivedAt),
                                    ZoneId.of("Europe/Berlin")
                                )
                            )
                        )
                    }
                }
            }).onItemClick {
                val bukkitPlayer = this.player
                val current = player.selectedTrophy

                if (current?.trophy?.uuid == trophy.trophy.uuid) {
                    player.selectedTrophy = null
                    bukkitPlayer.inventory.setItemInOffHand(null)
                } else {
                    player.selectedTrophy = trophy
                    bukkitPlayer.inventory.setItemInOffHand(trophy.trophy.item)
                }

                this.update()
            }
        }
    }

    layoutTarget('L')

    settings {
        paginationViewRows(PaginationViewRows.TWO)
    }

    onClose {
        plugin.launch {
            PlayerTrophyService.savePlayer(player)
        }
    }
}