package dev.slne.surf.trophy.api.player

import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import dev.slne.surf.trophy.api.trophy.ReceivedTrophy
import kotlinx.serialization.Serializable

@Serializable
data class TrophyPlayer(
    val uuid: SerializableUUID,
    val name: String,
    val trophies: MutableList<ReceivedTrophy>,
    @Volatile var selectedTrophy: ReceivedTrophy?
)