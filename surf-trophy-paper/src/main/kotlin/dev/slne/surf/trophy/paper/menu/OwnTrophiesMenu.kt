package dev.slne.surf.trophy.paper.menu

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.event.cancel
import dev.slne.surf.surfapi.bukkit.api.inventory.dsl.menu
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.dateTimeFormatter
import dev.slne.surf.trophy.api.TrophyPlayer
import org.bukkit.Bukkit
import org.bukkit.Material

private val borderItem = GuiItem(buildItem(Material.GRAY_STAINED_GLASS_PANE) {
    displayName {
        text(" ")
    }
})

private const val width = 9
private const val height = 6

fun ownTrophiesMenu(player: TrophyPlayer) {
    menu(buildText { variableValue("Deine Trophäen".toSmallCaps()) }, 6) {
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

        val contentPane = PaginatedPane(1, 1, 7, 4)

        contentPane.populateWithItemStacks(player.trophies.map {
            it.trophy.item.apply {
                displayName {
                    variableValue(it.trophy.name)
                }

                buildLore {
                    emptyLine()
                    line {
                        variableValue("Beschreibung:".toSmallCaps())
                    }

                    line {
                        note(it.trophy.description)
                    }

                    emptyLine()

                    line {
                        variableValue("Erhalten am:".toSmallCaps())
                    }

                    line {
                        note(dateTimeFormatter.format(java.time.Instant.ofEpochMilli(it.receivedAt)))
                    }
                }
            }
        })

        addPane(outlinePane)
        addPane(contentPane)

        setOnGlobalDrag { it.cancel() }
        setOnGlobalClick { it.cancel() }
    }.show(Bukkit.getPlayer(player.uuid) ?: return)
}