package dev.slne.surf.trophy.paper.menu

import com.github.shynixn.mccoroutine.folia.launch
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
import dev.slne.surf.surfapi.core.api.messages.adventure.plain
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.dateTimeFormatter
import dev.slne.surf.trophy.api.player.TrophyPlayer
import dev.slne.surf.trophy.api.trophy.ReceivedTrophy
import dev.slne.surf.trophy.core.service.trophyPlayerService
import dev.slne.surf.trophy.paper.plugin
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

private val borderItem = GuiItem(buildItem(Material.GRAY_STAINED_GLASS_PANE) {
    displayName {
        text(" ")
    }
})

private const val width = 9
private const val height = 6

fun ownTrophiesMenu(player: TrophyPlayer) {
    if (player.trophies.isEmpty()) {
        val bukkitPlayer =
            Bukkit.getPlayer(player.uuid) ?: error("Player to trophy player not found")
        noTrophiesMenu(bukkitPlayer, bukkitPlayer)
        return
    }

    menu(buildText { note("Deine Trophäen".toSmallCaps(), TextDecoration.BOLD) }, 6) {
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

        contentPane.populateWithGuiItems(player.trophies.map {
            GuiItem(it.trophy.item.apply {
                displayName {
                    variableValue(it.trophy.name)
                }

                if (player.selectedTrophy?.trophy?.uuid == it.trophy.uuid) {
                    editMeta { meta ->
                        meta.setEnchantmentGlintOverride(true)
                    }
                } else {
                    editMeta { meta ->
                        meta.setEnchantmentGlintOverride(false)
                    }
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
            }) { event ->
                val bukkitPlayer = event.whoClicked as? Player ?: return@GuiItem
                val trophy = getTrophyByItem(event.currentItem, bukkitPlayer) ?: return@GuiItem

                if (player.selectedTrophy?.trophy?.uuid == trophy.trophy.uuid) {
                    player.selectedTrophy = null
                    bukkitPlayer.inventory.setItemInOffHand(player.selectedTrophy?.trophy?.item)
                    bukkitPlayer.sendText {
                        appendPrefix()
                        success("Du hast die Trophäe ")
                        variableValue(trophy.trophy.name)
                        success(" abgewählt.")
                    }
                    return@GuiItem
                }

                player.selectedTrophy = trophy
                bukkitPlayer.inventory.setItemInOffHand(player.selectedTrophy?.trophy?.item)

                bukkitPlayer.sendText {
                    appendPrefix()
                    success("Du hast die Trophäe ")
                    variableValue(trophy?.trophy?.name ?: "Unbekannt")
                    success(" ausgewählt.")
                }

                update()
            }
        })

        setOnClose {
            plugin.launch {
                trophyPlayerService.savePlayer(player)
            }
        }

        addPane(outlinePane)
        addPane(contentPane)

        setOnGlobalDrag { it.cancel() }
        setOnGlobalClick { it.cancel() }
    }.show(Bukkit.getPlayer(player.uuid) ?: return)
}

private fun getTrophyByItem(itemStack: ItemStack?, player: Player): ReceivedTrophy? {
    if (itemStack == null) {
        return null
    }
    val name = itemStack.displayName().plain().replace("[", "").replace("]", "")
    val trophyPlayer = trophyPlayerService.findPlayerByUuid(player.uniqueId) ?: return null
    val trophy = trophyPlayer.trophies.find { it.trophy.name == name }

    return trophy
}