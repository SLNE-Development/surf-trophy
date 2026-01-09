package dev.slne.surf.trophy.paper.menu

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
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
            "Fehler".toSmallCaps(),
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
            })

            addItem(errorItem, 0, 0)
        }

        addPane(outlinePane)
        addPane(errorPane)

        setOnGlobalDrag { it.cancel() }
        setOnGlobalClick { it.cancel() }
    }.show(player ?: error("Player is null"))
}