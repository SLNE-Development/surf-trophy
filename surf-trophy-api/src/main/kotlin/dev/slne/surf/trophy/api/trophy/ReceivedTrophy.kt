package dev.slne.surf.trophy.api.trophy

import kotlinx.serialization.Serializable

@Serializable
data class ReceivedTrophy(
    val trophy: Trophy,
    val receivedAt: Long
)
