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
import dev.slne.surf.trophy.core.service.trophyPlayerService
import dev.slne.surf.trophy.core.service.trophyService
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
        val trophyPlayer = trophyPlayerService.findPlayerByUuid(player.uniqueId)

        if (trophyPlayer == null) {
            player.sendText {
                appendPrefix()
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
                    trophyPlayerService.loadOrGetPlayerByUuid(it)
                } ?: run {
                    player.sendText {
                        appendPrefix()
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
                    trophyService.refreshTrophies()

                    executor.sendText {
                        appendPrefix()
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
                                appendPrefix()
                                error("Du musst ein Item in der Hand halten, um eine Trophäe zu erstellen.")
                            }
                            return@playerExecutor
                        }

                        plugin.launch {
                            val trophy = Trophy(
                                UUID.randomUUID(),
                                name,
                                description,
                                item
                            )

                            trophyService.cacheTrophy(trophy)
                            trophyService.saveTrophy(trophy)

                            player.sendText {
                                appendPrefix()
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
                        trophyService.deleteTrophy(trophy)

                        player.sendText {
                            appendPrefix()
                            success("Die Trophäe wurde gelöscht.")
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
                    append(pagination.renderComponent(trophyService.getTrophies()))
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
                                trophyPlayerService.loadOrGetPlayerByUuid(it)
                            } ?: run {
                                player.sendText {
                                    appendPrefix()
                                    error("Der Spieler wurde nicht gefunden.")
                                }
                                return@launch
                            }

                            trophyPlayerService.giveTrophy(targetPlayer, trophy)

                            player.sendText {
                                appendPrefix()
                                success("Die Trophäe wurde dem Spieler gegeben.")
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
                                trophyPlayerService.loadOrGetPlayerByUuid(it)
                            } ?: run {
                                player.sendText {
                                    appendPrefix()
                                    error("Der Spieler wurde nicht gefunden.")
                                }
                                return@launch
                            }

                            trophyPlayerService.takeTrophy(targetPlayer, trophy)

                            player.sendText {
                                appendPrefix()
                                success("Die Trophäe wurde dem Spieler genommen.")
                            }
                        }
                    }
                }
            }
        }
    }
}