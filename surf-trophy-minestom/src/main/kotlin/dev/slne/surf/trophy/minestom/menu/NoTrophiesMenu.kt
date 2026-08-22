package dev.slne.surf.trophy.minestom.menu

import dev.slne.surf.api.minestom.inventory.framework.dsl.slot
import dev.slne.surf.api.minestom.inventory.framework.dsl.withItem
import dev.slne.surf.api.minestom.inventory.framework.view.onFirstRender
import dev.slne.surf.api.minestom.inventory.framework.view.settings
import dev.slne.surf.api.minestom.inventory.framework.view.state.get
import dev.slne.surf.api.minestom.inventory.framework.view.state.initialState
import dev.slne.surf.api.minestom.inventory.framework.view.surfView
import dev.slne.surf.trophy.api.player.TrophyPlayer
import dev.slne.surf.trophy.core.client.message.noTrophiesDisplayName
import dev.slne.surf.trophy.core.client.message.noTrophiesLore
import net.minestom.server.item.Material

val noTrophiesMenu = surfView("Keine Trophaen") {
    val targetHolder = initialState<TrophyPlayer>("target")

    settings {
        rows(3)
        cancelAllInteractions()
    }

    onFirstRender {
        val target = targetHolder[this]
        val viewer = this.player

        slot(2, 5) {
            withItem(Material.BARRIER) {
                displayName(noTrophiesDisplayName(target.uuid == viewer.uuid, target.name))
                lore(*noTrophiesLore)
            }
        }
    }
}
