package dev.slne.surf.trophy.paper.menu

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.event.cancel
import dev.slne.surf.surfapi.bukkit.api.inventory.dsl.menu
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player

private val borderItem = GuiItem(buildItem(Material.GRAY_STAINED_GLASS_PANE) {
    displayName {
        text(" ")
    }
})

private const val width = 9
private const val height = 5

fun noTrophiesMenu(player: Player, viewer: Player) {
    menu(buildText {
        note(
            "Deine Trophäen".toSmallCaps(),
            TextDecoration.BOLD
        )
    }, height) {
        val outlinePane = StaticPane(0, 0, width, height).apply {
            for (y in 1 until height - 1) {
                addItem(borderItem, 0, y)
                addItem(borderItem, width - 1, y)
            }

            for (x in 0 until width) {
                addItem(borderItem, x, 0)
                addItem(borderItem, x, height - 1)
            }
        }

        val errorPane = StaticPane(4, 2, 1, 1).apply {
            val errorItem = GuiItem(buildItem(Material.BARRIER) {
                displayName {
                    if (player.uniqueId == viewer.uniqueId) {
                        error(
                            "Du hast noch keine Trophäen erhalten!".toSmallCaps(),
                            TextDecoration.BOLD
                        )
                    } else {
                        error(
                            "${player.name} hat noch keine Trophäen erhalten!".toSmallCaps(),
                            TextDecoration.BOLD
                        )
                    }
                }
                buildLore {
                    emptyLine()
                    line {
                        spacer("»")
                        appendSpace()
                        text("Trophäen sind Zeichen deiner Geschichte auf diesem Server.")
                    }
                    line {
                        spacer("»")
                        appendSpace()
                        text("Du erhältst sie durch besondere Leistungen –")
                    }
                    line {
                        spacer("»")
                        appendSpace()
                        text("etwa durch die Teilnahme an Events oder deine Abenteuer")
                    }
                    line {
                        spacer("»")
                        appendSpace()
                        text("auf unseren Survival-Servern.")
                    }

                    emptyLine()
                    line {
                        spacer("»")
                        appendSpace()
                        text("Einige Trophäen sind streng limitiert.")
                    }
                    line {
                        spacer("»")
                        appendSpace()
                        text("Bestimmte Auszeichnungen sind exklusiv für Teammitglieder")
                    }
                    line {
                        spacer("»")
                        appendSpace()
                        text("oder ausgewählte Persönlichkeiten reserviert.")
                    }

                    emptyLine()
                    line {
                        spacer("»")
                        appendSpace()
                        text("Jede Trophäe erzählt ihre eigene Geschichte –")
                    }
                    line {
                        spacer("»")
                        appendSpace()
                        text("vielleicht bald auch deine.")
                    }
                }
            })

            addItem(errorItem, 0, 0)
        }

        addPane(outlinePane)
        addPane(errorPane)

        setOnGlobalDrag { it.cancel() }
        setOnGlobalClick { it.cancel() }
    }.show(player)
}