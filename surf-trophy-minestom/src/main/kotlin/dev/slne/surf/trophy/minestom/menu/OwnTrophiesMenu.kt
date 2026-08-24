package dev.slne.surf.trophy.minestom.menu

import dev.slne.minestom.lobby.api.coroutine.minestomAsyncScope
import dev.slne.surf.api.minestom.inventory.framework.dsl.onItemClick
import dev.slne.surf.api.minestom.inventory.framework.view.layoutTarget
import dev.slne.surf.api.minestom.inventory.framework.view.onClose
import dev.slne.surf.api.minestom.inventory.framework.view.paginatedSurfView
import dev.slne.surf.api.minestom.inventory.framework.view.pagination.pagination
import dev.slne.surf.api.minestom.inventory.framework.view.settings
import dev.slne.surf.api.minestom.inventory.framework.view.settings.PaginationViewRows
import dev.slne.surf.api.minestom.inventory.framework.view.state.get
import dev.slne.surf.api.minestom.inventory.framework.view.state.initialState
import dev.slne.surf.trophy.api.player.TrophyPlayer
import dev.slne.surf.trophy.core.client.player.trophiesByReceivedAt
import dev.slne.surf.trophy.core.common.service.PlayerTrophyService
import dev.slne.surf.trophy.minestom.util.trophyItem
import kotlinx.coroutines.launch
import net.minestom.server.item.ItemStack

val ownTrophiesMenu = paginatedSurfView("Trophaen") {
    val playerHolder = initialState<TrophyPlayer>("player")
    pagination {
        lazySource { context -> playerHolder[context].trophiesByReceivedAt() }

        elementFactory { _, builder, _, trophy ->
            builder.withItem(trophyItem(trophy)).onItemClick {
                val player = playerHolder[this]
                val minestomPlayer = this.player
                val current = player.selectedTrophy

                if (current?.trophy?.uuid == trophy.trophy.uuid) {
                    player.selectedTrophy = null
                    minestomPlayer.itemInOffHand = ItemStack.AIR
                } else {
                    player.selectedTrophy = trophy
                    minestomPlayer.itemInOffHand = trophyItem(trophy)
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
        val player = playerHolder[this]
        minestomAsyncScope.launch {
            PlayerTrophyService.saveSelectedTrophy(player)
        }
    }
}
