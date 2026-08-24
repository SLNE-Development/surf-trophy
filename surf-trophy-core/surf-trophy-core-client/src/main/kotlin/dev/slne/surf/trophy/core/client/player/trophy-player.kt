package dev.slne.surf.trophy.core.client.player

import dev.slne.surf.api.core.util.mutableObjectListOf
import dev.slne.surf.trophy.api.player.TrophyPlayer
import dev.slne.surf.trophy.api.trophy.ReceivedTrophy
import it.unimi.dsi.fastutil.objects.ObjectArrayList

/**
 * Orders received trophies by the moment they were received.
 */
private val byReceivedAt: Comparator<ReceivedTrophy> =
    Comparator.comparingLong(ReceivedTrophy::receivedAt)

/**
 * A stable copy of this player's trophies.
 */
fun TrophyPlayer.trophiesSnapshot(): List<ReceivedTrophy> = copyTrophies()

/**
 * This player's trophies, oldest first
 */
fun TrophyPlayer.trophiesByReceivedAt(): List<ReceivedTrophy> =
    copyTrophies().apply { sortWith(byReceivedAt) }

private fun TrophyPlayer.copyTrophies(): ObjectArrayList<ReceivedTrophy> =
    synchronized(trophies) { mutableObjectListOf(trophies) }
