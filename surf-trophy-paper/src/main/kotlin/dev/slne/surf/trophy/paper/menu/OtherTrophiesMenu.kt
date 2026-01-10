package dev.slne.surf.trophy.paper.menu

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.event.cancel
import dev.slne.surf.surfapi.bukkit.api.inventory.dsl.menu
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.dateTimeFormatter
import dev.slne.surf.trophy.api.player.TrophyPlayer
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

private val borderItem = GuiItem(buildItem(Material.GRAY_STAINED_GLASS_PANE) {
    displayName {
        text(" ")
    }
})

private const val width = 9
private const val height = 4

fun otherTrophiesMenu(player: TrophyPlayer, viewer: Player) {
    if (player.trophies.isEmpty()) {
        noTrophiesMenu(
            Bukkit.getPlayer(player.uuid) ?: error("Player to trophy player not found"),
            viewer
        )
        return
    }

    menu(
        buildText { note("${player.name}'s Trophäen".toSmallCaps(), TextDecoration.BOLD) },
        height
    ) {
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

        val selectedPane = StaticPane(4, 0, 1, 1, Pane.Priority.HIGHEST).apply {
            player.selectedTrophy?.let {
                addItem(
                    GuiItem(it.trophy.item.apply {
                        displayName {
                            variableValue("Ausgewählte Trophäe".toSmallCaps())
                        }

                        buildLore {
                            emptyLine()
                            line {
                                variableValue("Name:".toSmallCaps())
                            }

                            line {
                                note(it.trophy.name)
                            }

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
                                note(
                                    dateTimeFormatter.format(
                                        ZonedDateTime.ofInstant(
                                            Instant.ofEpochMilli(it.receivedAt),
                                            ZoneId.of("Europe/Berlin")
                                        )
                                    )
                                )
                            }
                        }
                    }),
                    0,
                    0
                )
            }
        }

        val contentPane = PaginatedPane(1, 1, 7, height - 2)

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
                        note(
                            dateTimeFormatter.format(
                                ZonedDateTime.ofInstant(
                                    Instant.ofEpochMilli(it.receivedAt),
                                    ZoneId.of("Europe/Berlin")
                                )
                            )
                        )
                    }
                }
            }
        })

        val navigationPane = StaticPane(0, height - 1, 7, 1).apply {
            if (contentPane.page > 1) {
                addItem(
                    GuiItem(buildItem(Material.ARROW) {
                        displayName {
                            variableValue("Vorherige Seite".toSmallCaps())
                        }
                    }) {
                        contentPane.page -= 1
                        update()
                    }, 0, 0
                )
            }

            if (contentPane.page < contentPane.pages - 1) {
                addItem(
                    GuiItem(buildItem(Material.ARROW) {
                        displayName {
                            variableValue("Nächste Seite".toSmallCaps())
                        }
                    }) {
                        contentPane.page += 1
                        update()
                    }, 6, 0
                )
            }
        }

        addPane(navigationPane)

        addPane(outlinePane)
        addPane(contentPane)

        player.selectedTrophy?.let {
            addPane(selectedPane)
        }

        setOnGlobalDrag { it.cancel() }
        setOnGlobalClick { it.cancel() }
    }.show(Bukkit.getPlayer(viewer.uniqueId) ?: return)
}