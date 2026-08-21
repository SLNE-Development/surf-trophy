package dev.slne.surf.trophy.core.client.command

import dev.slne.surf.trophy.api.trophy.Trophy
import dev.slne.surf.trophy.core.common.service.PlayerTrophyService
import net.kyori.adventure.text.Component
import java.util.*

/**
 * The result of granting or revoking a trophy for a command target.
 */
enum class TrophyMutationResult {
    /** The target could not be resolved. */
    PLAYER_NOT_FOUND,

    /** The target already owned the trophy, or did not own it at all. */
    REJECTED,

    /** The trophy was granted or revoked. */
    SUCCESS,
}

/**
 * Grants [trophy] to the player identified by [targetUuid].
 */
suspend fun giveTrophy(targetUuid: UUID?, trophy: Trophy): TrophyMutationResult {
    val target = loadTarget(targetUuid) ?: return TrophyMutationResult.PLAYER_NOT_FOUND

    return if (PlayerTrophyService.giveTrophy(target, trophy)) {
        TrophyMutationResult.SUCCESS
    } else {
        TrophyMutationResult.REJECTED
    }
}

/**
 * Revokes [trophy] from the player identified by [targetUuid].
 */
suspend fun takeTrophy(targetUuid: UUID?, trophy: Trophy): TrophyMutationResult {
    val target = loadTarget(targetUuid) ?: return TrophyMutationResult.PLAYER_NOT_FOUND

    return if (PlayerTrophyService.takeTrophy(target, trophy)) {
        TrophyMutationResult.SUCCESS
    } else {
        TrophyMutationResult.REJECTED
    }
}

/** The message reporting the outcome of [giveTrophy]. */
fun TrophyMutationResult.giveMessage(): Component = when (this) {
    TrophyMutationResult.PLAYER_NOT_FOUND -> TrophyMessages.playerNotFound
    TrophyMutationResult.REJECTED -> TrophyMessages.trophyAlreadyOwned
    TrophyMutationResult.SUCCESS -> TrophyMessages.trophyGiven
}

/** The message reporting the outcome of [takeTrophy]. */
fun TrophyMutationResult.takeMessage(): Component = when (this) {
    TrophyMutationResult.PLAYER_NOT_FOUND -> TrophyMessages.playerNotFound
    TrophyMutationResult.REJECTED -> TrophyMessages.trophyNotOwned
    TrophyMutationResult.SUCCESS -> TrophyMessages.trophyTaken
}

private suspend fun loadTarget(targetUuid: UUID?) =
    targetUuid?.let { PlayerTrophyService.loadOrGetPlayerByUuid(it) }
