package dev.slne.surf.trophy.paper.menu

import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.paper.builder.buildItem
import dev.slne.surf.api.paper.builder.buildLore
import dev.slne.surf.api.paper.builder.displayName
import dev.slne.surf.api.paper.inventory.framework.dsl.slot
import dev.slne.surf.api.paper.inventory.framework.view.*
import dev.slne.surf.api.paper.inventory.framework.view.container.dsl.blockRow
import dev.slne.surf.api.paper.inventory.framework.view.state.get
import dev.slne.surf.api.paper.inventory.framework.view.state.initialState
import dev.slne.surf.trophy.api.player.TrophyPlayer
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material

fun noTrophiesMenu(): AbstractSurfView =
    surfView("Keine Trophäen") {
        val targetHolder = initialState<TrophyPlayer>("target")

        settings {
            rows(3)
            cancelAllInteractions()
        }

        containerDefaults {
            blockRow(1)
            blockRow(2)
            blockRow(3)
        }

        onFirstRender {
            val target = targetHolder[this]
            val viewer = this.player

            slot(2, 5) {
                withItem(buildItem(Material.BARRIER) {
                    displayName {
                        if (target.uuid == viewer.uniqueId) {
                            error(
                                "Du hast noch keine Trophäen erhalten!".toSmallCaps(),
                                TextDecoration.BOLD
                            )
                        } else {
                            error(
                                "${target.name} hat noch keine Trophäen erhalten!".toSmallCaps(),
                                TextDecoration.BOLD
                            )
                        }
                    }

                    buildLore {
                        emptyLine()
                        line { spacer("»"); appendSpace(); info("Trophäen sind Zeichen deiner Geschichte auf diesem Server.") }
                        line { spacer("»"); appendSpace(); info("Du erhältst sie durch besondere Leistungen –") }
                        line { spacer("»"); appendSpace(); info("etwa durch Events oder Abenteuer.") }

                        emptyLine()
                        line { spacer("»"); appendSpace(); info("Einige Trophäen sind streng limitiert.") }
                        line { spacer("»"); appendSpace(); info("Manche sind exklusiv für besondere Spieler.") }

                        emptyLine()
                        line { spacer("»"); appendSpace(); info("Jede Trophäe erzählt eine Geschichte.") }
                    }
                })
            }
        }
    }