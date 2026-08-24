package dev.slne.surf.trophy.paper.menu

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.api.paper.inventory.framework.dsl.onItemClick
import dev.slne.surf.api.paper.inventory.framework.view.layoutTarget
import dev.slne.surf.api.paper.inventory.framework.view.onClose
import dev.slne.surf.api.paper.inventory.framework.view.paginatedSurfView
import dev.slne.surf.api.paper.inventory.framework.view.pagination.pagination
import dev.slne.surf.api.paper.inventory.framework.view.settings
import dev.slne.surf.api.paper.inventory.framework.view.settings.PaginationViewRows
import dev.slne.surf.api.paper.inventory.framework.view.state.get
import dev.slne.surf.api.paper.inventory.framework.view.state.initialState
import dev.slne.surf.trophy.api.player.TrophyPlayer
import dev.slne.surf.trophy.core.client.player.trophiesByReceivedAt
import dev.slne.surf.trophy.core.common.service.PlayerTrophyService
import dev.slne.surf.trophy.paper.plugin
import dev.slne.surf.trophy.paper.util.trophyItem

val ownTrophiesMenu = paginatedSurfView("Trophaen") {
    val playerHolder = initialState<TrophyPlayer>("player")
    pagination {
        lazySource { context -> playerHolder[context].trophiesByReceivedAt() }

        elementFactory { _, builder, _, trophy ->
            builder.withItem(trophyItem(trophy)).onItemClick {
                val player = playerHolder[this]
                val bukkitPlayer = this.player
                val current = player.selectedTrophy

                if (current?.trophy?.uuid == trophy.trophy.uuid) {
                    player.selectedTrophy = null
                    bukkitPlayer.inventory.setItemInOffHand(null)
                } else {
                    player.selectedTrophy = trophy
                    bukkitPlayer.inventory.setItemInOffHand(trophyItem(trophy))
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
        plugin.launch {
            PlayerTrophyService.saveSelectedTrophy(player)
        }
    }
}
