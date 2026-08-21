package dev.slne.surf.trophy.paper.menu

import dev.slne.surf.api.paper.builder.displayName
import dev.slne.surf.api.paper.builder.lore
import dev.slne.surf.api.paper.inventory.framework.dsl.slot
import dev.slne.surf.api.paper.inventory.framework.dsl.withItem
import dev.slne.surf.api.paper.inventory.framework.view.AbstractSurfView
import dev.slne.surf.api.paper.inventory.framework.view.onFirstRender
import dev.slne.surf.api.paper.inventory.framework.view.settings
import dev.slne.surf.api.paper.inventory.framework.view.state.get
import dev.slne.surf.api.paper.inventory.framework.view.state.initialState
import dev.slne.surf.api.paper.inventory.framework.view.surfView
import dev.slne.surf.trophy.api.player.TrophyPlayer
import dev.slne.surf.trophy.core.client.message.noTrophiesDisplayName
import dev.slne.surf.trophy.core.client.message.noTrophiesLore
import org.bukkit.Material

fun noTrophiesMenu(): AbstractSurfView =
    surfView("Keine Trophaen") {
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
                    displayName(noTrophiesDisplayName(target.uuid == viewer.uniqueId, target.name))
                    lore(*noTrophiesLore)
                }
            }
        }
    }
