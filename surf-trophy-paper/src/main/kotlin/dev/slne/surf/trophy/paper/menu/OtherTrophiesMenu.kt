package dev.slne.surf.trophy.paper.menu

import dev.slne.surf.api.paper.builder.displayName
import dev.slne.surf.api.paper.builder.lore
import dev.slne.surf.api.paper.inventory.framework.dsl.slot
import dev.slne.surf.api.paper.inventory.framework.view.layoutTarget
import dev.slne.surf.api.paper.inventory.framework.view.onFirstRender
import dev.slne.surf.api.paper.inventory.framework.view.paginatedSurfView
import dev.slne.surf.api.paper.inventory.framework.view.pagination.pagination
import dev.slne.surf.api.paper.inventory.framework.view.settings
import dev.slne.surf.api.paper.inventory.framework.view.settings.PaginationViewRows
import dev.slne.surf.api.paper.inventory.framework.view.state.get
import dev.slne.surf.api.paper.inventory.framework.view.state.initialState
import dev.slne.surf.trophy.api.player.TrophyPlayer
import dev.slne.surf.trophy.core.client.message.receivedTrophyLore
import dev.slne.surf.trophy.core.client.message.selectedTrophyDisplayName
import dev.slne.surf.trophy.core.client.message.trophyDisplayName
import dev.slne.surf.trophy.paper.util.item

fun otherTrophiesMenu() = paginatedSurfView("Trophäen") {
    val playerHolder = initialState<TrophyPlayer>("player")

    pagination {
        lazySource { context ->
            playerHolder[context].trophies.sortedBy { it.receivedAt }
        }

        elementFactory { _, builder, _, trophy ->
            builder.withItem(trophy.trophy.item.apply {
                displayName(trophyDisplayName(trophy.trophy))
                lore(*receivedTrophyLore(trophy).toTypedArray())
            })
        }
    }

    layoutTarget('L')

    onFirstRender {
        val player = playerHolder[this]
        player.selectedTrophy?.let { selected ->
            slot(4, 1) {
                withItem(selected.trophy.item.apply {
                    displayName(selectedTrophyDisplayName)
                })
            }
        }
    }

    settings {
        paginationViewRows(PaginationViewRows.TWO)
    }
}
