package dev.slne.surf.trophy.paper.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.api.paper.inventory.framework.open
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.paper.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.trophy.api.trophy.Trophy
import dev.slne.surf.trophy.core.client.command.TrophyMessages
import dev.slne.surf.trophy.core.client.command.giveMessage
import dev.slne.surf.trophy.core.client.command.giveTrophy
import dev.slne.surf.trophy.core.client.command.takeMessage
import dev.slne.surf.trophy.core.client.command.takeTrophy
import dev.slne.surf.trophy.core.common.service.PlayerTrophyService
import dev.slne.surf.trophy.core.common.service.TrophyService
import dev.slne.surf.trophy.paper.command.argument.trophyArgument
import dev.slne.surf.trophy.paper.menu.noTrophiesMenu
import dev.slne.surf.trophy.paper.menu.otherTrophiesMenu
import dev.slne.surf.trophy.paper.menu.ownTrophiesMenu
import dev.slne.surf.trophy.paper.permission.PermissionRegistry
import dev.slne.surf.trophy.paper.plugin
import dev.slne.surf.trophy.paper.util.itemStackToString
import kotlinx.coroutines.Deferred
import java.util.*

fun trophyCommand() = commandTree("trophy") {
    withPermission(PermissionRegistry.COMMAND_TROPHY)
    playerExecutor { player, _ ->
        val trophyPlayer = PlayerTrophyService.findPlayerByUuid(player.uniqueId)

        if (trophyPlayer == null) {
            player.sendMessage(TrophyMessages.dataNotLoaded)
            return@playerExecutor
        }

        ownTrophiesMenu().open(player, mapOf("player" to trophyPlayer))
    }

    surfOfflinePlayerArgument("target") {
        withPermission(PermissionRegistry.COMMAND_TROPHY_OTHERS)

        playerExecutor { player, args ->
            val target: Deferred<SurfPlayer?> by args

            plugin.launch {
                val targetPlayer = target.await()?.uuid?.let {
                    PlayerTrophyService.loadOrGetPlayerByUuid(it)
                } ?: run {
                    player.sendMessage(TrophyMessages.playerNotFound)
                    return@launch
                }

                if (targetPlayer.trophies.isEmpty()) {
                    noTrophiesMenu().open(player, mapOf("target" to targetPlayer))
                }

                otherTrophiesMenu().open(player, mapOf("player" to targetPlayer))
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

                    executor.sendMessage(TrophyMessages.trophiesRefreshed)
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
                            player.sendMessage(TrophyMessages.noItemInHand)
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

                            player.sendMessage(TrophyMessages.trophyCreated)
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
                            player.sendMessage(TrophyMessages.trophyDeleted)
                        } else {
                            player.sendMessage(TrophyMessages.trophyNotFound)
                        }
                    }
                }
            }
        }

        literalArgument("list") {
            anyExecutor { executor, _ ->
                executor.sendMessage(TrophyMessages.trophyList(TrophyService.getTrophies()))
            }
        }

        literalArgument("give") {
            surfOfflinePlayerArgument("target") {
                trophyArgument("trophy") {
                    playerExecutor { player, args ->
                        val target: Deferred<SurfPlayer?> by args
                        val trophy: Trophy by args

                        plugin.launch {
                            player.sendMessage(
                                giveTrophy(target.await()?.uuid, trophy).giveMessage()
                            )
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
                            player.sendMessage(
                                takeTrophy(target.await()?.uuid, trophy).takeMessage()
                            )
                        }
                    }
                }
            }
        }
    }
}
