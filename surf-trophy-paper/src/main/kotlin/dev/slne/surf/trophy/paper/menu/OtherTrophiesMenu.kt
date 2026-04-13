package dev.slne.surf.trophy.paper.menu

import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.util.dateTimeFormatter
import dev.slne.surf.api.paper.builder.buildLore
import dev.slne.surf.api.paper.builder.displayName
import dev.slne.surf.api.paper.inventory.framework.dsl.slot
import dev.slne.surf.api.paper.inventory.framework.view.layoutTarget
import dev.slne.surf.api.paper.inventory.framework.view.onFirstRender
import dev.slne.surf.api.paper.inventory.framework.view.paginatedSurfView
import dev.slne.surf.api.paper.inventory.framework.view.pagination.pagination
import dev.slne.surf.api.paper.inventory.framework.view.settings
import dev.slne.surf.api.paper.inventory.framework.view.settings.PaginationViewRows
import dev.slne.surf.trophy.api.player.TrophyPlayer
import dev.slne.surf.trophy.core.paper.util.item
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

fun otherTrophiesMenu(player: TrophyPlayer) = paginatedSurfView("${player.name}'s Trophäen") {
    pagination {
        lazySource { player.trophies }

        elementFactory { _, builder, _, trophy ->
            builder.withItem(trophy.trophy.item.apply {
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
            })
        }
    }

    layoutTarget('L')

    onFirstRender {
        player.selectedTrophy?.let { selected ->
            slot(5, 1) {
                withItem(selected.trophy.item.apply {
                    displayName { variableValue("Ausgewählt".toSmallCaps()) }
                })
            }
        }
    }

    settings {
        paginationViewRows(PaginationViewRows.TWO)
    }
}