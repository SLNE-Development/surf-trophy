package dev.slne.surf.trophy.api.trophy

import dev.slne.surf.surfapi.core.api.serializer.java.uuid.SerializableUUID
import kotlinx.serialization.Serializable

@Serializable
data class Trophy(
    val uuid: SerializableUUID,
    val name: String,
    val description: String,
    val itemString: String
)
