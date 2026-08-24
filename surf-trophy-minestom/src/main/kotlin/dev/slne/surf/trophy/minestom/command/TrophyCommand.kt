package dev.slne.surf.trophy.minestom.command

import dev.slne.minestom.lobby.api.command.commandapi.dsl.anyExecutor
import dev.slne.minestom.lobby.api.command.commandapi.dsl.anyExecutorSuspend
import dev.slne.minestom.lobby.api.command.commandapi.dsl.commandTree
import dev.slne.minestom.lobby.api.command.commandapi.dsl.literalArgument
import dev.slne.minestom.lobby.api.command.commandapi.dsl.playerExecutor
import dev.slne.minestom.lobby.api.command.commandapi.dsl.playerExecutorSuspend
import dev.slne.minestom.lobby.api.coroutine.MinestomDispatchers
import dev.slne.surf.api.minestom.inventory.framework.open
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.minestom.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.trophy.api.player.TrophyPlayer
import dev.slne.surf.trophy.api.trophy.Trophy
import dev.slne.surf.trophy.core.client.command.TrophyMessages
import dev.slne.surf.trophy.core.client.command.giveMessage
import dev.slne.surf.trophy.core.client.command.giveTrophy
import dev.slne.surf.trophy.core.client.command.takeMessage
import dev.slne.surf.trophy.core.client.command.takeTrophy
import dev.slne.surf.trophy.core.client.permission.TrophyPermissions
import dev.slne.surf.trophy.core.common.service.PlayerTrophyService
import dev.slne.surf.trophy.core.common.service.TrophyService
import dev.slne.surf.trophy.minestom.command.argument.trophyArgument
import dev.slne.surf.trophy.minestom.menu.noTrophiesMenu
import dev.slne.surf.trophy.minestom.menu.otherTrophiesMenu
import dev.slne.surf.trophy.minestom.menu.ownTrophiesMenu
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.withContext
import net.minestom.server.entity.Player

fun trophyCommand() = commandTree("trophy") {
    withPermission(TrophyPermissions.COMMAND_TROPHY)
    playerExecutor { player, _ ->
        val trophyPlayer = PlayerTrophyService.findPlayerByUuid(player.uuid)

        if (trophyPlayer == null) {
            player.sendMessage(TrophyMessages.dataNotLoaded)
            return@playerExecutor
        }

        ownTrophiesMenu.open(player, mapOf("player" to trophyPlayer))
    }

    surfOfflinePlayerArgument("target") {
        withPermission(TrophyPermissions.COMMAND_TROPHY_OTHERS)

        playerExecutorSuspend { player, args ->
            val target: Deferred<SurfPlayer?> by args

            val targetPlayer = target.await()?.uuid?.let {
                PlayerTrophyService.loadOrGetPlayerByUuid(it)
            } ?: run {
                player.sendMessage(TrophyMessages.playerNotFound)
                return@playerExecutorSuspend
            }

            player.openTrophies(targetPlayer)
        }
    }

    literalArgument("manage") {
        withPermission(TrophyPermissions.COMMAND_TROPHY_MANAGE)

        literalArgument("refresh") {
            withPermission(TrophyPermissions.COMMAND_TROPHY_REFRESH)

            anyExecutorSuspend { executor, _ ->
                TrophyService.refreshTrophies()

                executor.sendMessage(TrophyMessages.trophiesRefreshed)
            }
        }

        literalArgument("delete") {
            trophyArgument("trophy") {
                playerExecutorSuspend { player, args ->
                    val trophy: Trophy by args

                    if (TrophyService.deleteTrophy(trophy)) {
                        player.sendMessage(TrophyMessages.trophyDeleted)
                    } else {
                        player.sendMessage(TrophyMessages.trophyNotFound)
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
                    playerExecutorSuspend { player, args ->
                        val target: Deferred<SurfPlayer?> by args
                        val trophy: Trophy by args

                        player.sendMessage(
                            giveTrophy(target.await()?.uuid, trophy).giveMessage()
                        )
                    }
                }
            }
        }

        literalArgument("take") {
            surfOfflinePlayerArgument("target") {
                trophyArgument("trophy") {
                    playerExecutorSuspend { player, args ->
                        val target: Deferred<SurfPlayer?> by args
                        val trophy: Trophy by args

                        player.sendMessage(
                            takeTrophy(target.await()?.uuid, trophy).takeMessage()
                        )
                    }
                }
            }
        }
    }
}

private suspend fun Player.openTrophies(target: TrophyPlayer) =
    withContext(MinestomDispatchers.Main) {
        if (target.trophies.isEmpty()) {
            noTrophiesMenu.open(this@openTrophies, mapOf("target" to target))
        } else {
            otherTrophiesMenu.open(this@openTrophies, mapOf("player" to target))
        }
    }
