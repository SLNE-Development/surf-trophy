package dev.slne.surf.trophy.paper.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.paper.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import dev.slne.surf.trophy.api.trophy.Trophy
import dev.slne.surf.trophy.core.common.service.PlayerTrophyService
import dev.slne.surf.trophy.core.common.service.TrophyService
import dev.slne.surf.trophy.core.paper.util.itemStackToString
import dev.slne.surf.trophy.paper.command.argument.trophyArgument
import dev.slne.surf.trophy.paper.menu.otherTrophiesMenu
import dev.slne.surf.trophy.paper.menu.ownTrophiesMenu
import dev.slne.surf.trophy.paper.permission.PermissionRegistry
import dev.slne.surf.trophy.paper.plugin
import kotlinx.coroutines.Deferred
import net.kyori.adventure.text.format.TextDecoration
import java.util.*

fun trophyCommand() = commandTree("trophy") {
    withPermission(PermissionRegistry.COMMAND_TROPHY)
    playerExecutor { player, _ ->
        val trophyPlayer = PlayerTrophyService.findPlayerByUuid(player.uniqueId)

        if (trophyPlayer == null) {
            player.sendText {
                appendErrorPrefix()
                error("Deine Daten konnten nicht geladen werden.")
            }
            return@playerExecutor
        }

        ownTrophiesMenu(trophyPlayer)
    }

    surfOfflinePlayerArgument("target") {
        withPermission(PermissionRegistry.COMMAND_TROPHY_OTHERS)

        playerExecutor { player, args ->
            val target: Deferred<SurfPlayer?> by args

            plugin.launch {
                val targetPlayer = target.await()?.uuid?.let {
                    PlayerTrophyService.loadOrGetPlayerByUuid(it)
                } ?: run {
                    player.sendText {
                        appendErrorPrefix()
                        error("Der Spieler wurde nicht gefunden.")
                    }
                    return@launch
                }

                otherTrophiesMenu(targetPlayer, player)
            }
        }
    }

    literalArgument("manage") {
        withPermission(PermissionRegistry.COMMAND_TROPHY_MANAGE)

        literalArgument("refresh") {
            withPermission(PermissionRegistry.COMMAND_TROPHY_REFRESH)

            anyExecutor { executor, _ ->
                plugin.launch {
                    TrophyService.refreshTrophies()

                    executor.sendText {
                        appendSuccessPrefix()
                        success("Alle Trophäen wurden neu geladen.")
                    }
                }
            }
        }

        literalArgument("create") {
            textArgument("name") {
                textArgument("description") {
                    playerExecutor { player, args ->
                        val name: String by args
                        val description: String by args

                        val item = player.inventory.itemInMainHand

                        if (item.isEmpty) {
                            player.sendText {
                                appendErrorPrefix()
                                error("Du musst ein Item in der Hand halten, um eine Trophäe zu erstellen.")
                            }
                            return@playerExecutor
                        }

                        plugin.launch {
                            val trophy = Trophy(
                                UUID.randomUUID(),
                                name,
                                description,
                                itemStackToString(item)
                            )

                            TrophyService.cacheTrophy(trophy)
                            TrophyService.saveTrophy(trophy)

                            player.sendText {
                                appendSuccessPrefix()
                                success("Die Trophäe wurde erstellt.")
                            }
                        }
                    }
                }
            }
        }

        literalArgument("delete") {
            trophyArgument("trophy") {
                playerExecutor { player, args ->
                    val trophy: Trophy by args

                    plugin.launch {
                        if (TrophyService.deleteTrophy(trophy)) {
                            player.sendText {
                                appendSuccessPrefix()
                                success("Die Trophäe wurde gelöscht.")
                            }
                        } else {
                            player.sendText {
                                appendErrorPrefix()
                                error("Die Trophäe wurde nicht gefunden.")
                            }
                        }
                    }
                }
            }
        }

        literalArgument("list") {
            anyExecutor { executor, _ ->
                val pagination = Pagination<Trophy> {
                    title { primary("Trophäen".toSmallCaps(), TextDecoration.BOLD) }
                    rowRenderer { trophy, _ ->
                        listOf(buildText {
                            spacer("- ")
                            variableValue(trophy.name)

                            hoverEvent(buildText {
                                variableValue(trophy.description)
                            })
                        })
                    }
                }

                executor.sendText {
                    appendNewline()
                    append(pagination.renderComponent(TrophyService.getTrophies()))
                }
            }
        }

        literalArgument("give") {
            surfOfflinePlayerArgument("target") {
                trophyArgument("trophy") {
                    playerExecutor { player, args ->
                        val target: Deferred<SurfPlayer?> by args
                        val trophy: Trophy by args

                        plugin.launch {
                            val targetPlayer = target.await()?.uuid?.let {
                                PlayerTrophyService.loadOrGetPlayerByUuid(it)
                            } ?: run {
                                player.sendText {
                                    appendErrorPrefix()
                                    error("Der Spieler wurde nicht gefunden.")
                                }
                                return@launch
                            }

                            if (PlayerTrophyService.giveTrophy(targetPlayer, trophy)) {
                                player.sendText {
                                    appendSuccessPrefix()
                                    success("Die Trophäe wurde dem Spieler gegeben.")
                                }
                            } else {
                                player.sendText {
                                    appendErrorPrefix()
                                    error("Der Spieler besitzt die Trophäe bereits.")
                                }
                            }
                        }
                    }
                }
            }
        }

        literalArgument("take") {
            surfOfflinePlayerArgument("target") {
                trophyArgument("trophy") {
                    playerExecutor { player, args ->
                        val target: Deferred<SurfPlayer?> by args
                        val trophy: Trophy by args

                        plugin.launch {
                            val targetPlayer = target.await()?.uuid?.let {
                                PlayerTrophyService.loadOrGetPlayerByUuid(it)
                            } ?: run {
                                player.sendText {
                                    appendErrorPrefix()
                                    error("Der Spieler wurde nicht gefunden.")
                                }
                                return@launch
                            }

                            if (PlayerTrophyService.takeTrophy(targetPlayer, trophy)) {
                                player.sendText {
                                    appendSuccessPrefix()
                                    success("Die Trophäe wurde dem Spieler genommen.")
                                }
                            } else {
                                player.sendText {
                                    appendErrorPrefix()
                                    error("Der Spieler besitzt die Trophäe nicht.")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}