package dev.slne.surf.trophy.minestom.menu

import dev.slne.surf.api.minestom.inventory.framework.dsl.slot
import dev.slne.surf.api.minestom.inventory.framework.view.layoutTarget
import dev.slne.surf.api.minestom.inventory.framework.view.onFirstRender
import dev.slne.surf.api.minestom.inventory.framework.view.paginatedSurfView
import dev.slne.surf.api.minestom.inventory.framework.view.pagination.pagination
import dev.slne.surf.api.minestom.inventory.framework.view.settings
import dev.slne.surf.api.minestom.inventory.framework.view.settings.PaginationViewRows
import dev.slne.surf.api.minestom.inventory.framework.view.state.get
import dev.slne.surf.api.minestom.inventory.framework.view.state.initialState
import dev.slne.surf.trophy.api.player.TrophyPlayer
import dev.slne.surf.trophy.core.client.message.selectedTrophyDisplayName
import dev.slne.surf.trophy.minestom.util.item
import dev.slne.surf.trophy.minestom.util.trophyItem
import dev.slne.surf.trophy.minestom.util.withDisplayName

val otherTrophiesMenu = paginatedSurfView("Trophäen") {
    val playerHolder = initialState<TrophyPlayer>("player")

    pagination {
        lazySource { context ->
            playerHolder[context].trophies.sortedBy { it.receivedAt }
        }

        elementFactory { _, builder, _, trophy ->
            builder.withItem(trophyItem(trophy))
        }
    }

    layoutTarget('L')

    onFirstRender {
        val player = playerHolder[this]
        player.selectedTrophy?.let { selected ->
            slot(4, 1) {
                withItem(selected.trophy.item.withDisplayName(selectedTrophyDisplayName))
            }
        }
    }

    settings {
        paginationViewRows(PaginationViewRows.TWO)
    }
}
