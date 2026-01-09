package dev.slne.surf.trophy.paper.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.paper.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.trophy.core.service.trophyPlayerService
import dev.slne.surf.trophy.paper.menu.otherTrophiesMenu
import dev.slne.surf.trophy.paper.menu.ownTrophiesMenu
import dev.slne.surf.trophy.paper.permission.PermissionRegistry
import dev.slne.surf.trophy.paper.plugin
import kotlinx.coroutines.Deferred

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

                otherTrophiesMenu(targetPlayer)
            }
        }
    }

    literalArgument("reload") {
        withPermission(PermissionRegistry.COMMAND_TROPHY_RELOAD)
    }

    literalArgument("refresh") {
        withPermission(PermissionRegistry.COMMAND_TROPHY_REFRESH)
    }
}