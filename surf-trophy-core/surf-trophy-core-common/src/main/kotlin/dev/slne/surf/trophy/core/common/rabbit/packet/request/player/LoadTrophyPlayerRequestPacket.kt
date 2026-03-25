package dev.slne.surf.trophy.core.common.rabbit.packet.request.player

import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import dev.slne.surf.surfapi.core.api.serializer.java.uuid.SerializableUUID
import dev.slne.surf.trophy.core.common.rabbit.packet.response.NullableTrophyPlayerResponsePacket
import kotlinx.serialization.Serializable

@Serializable
data class LoadTrophyPlayerRequestPacket(
    val uuid: SerializableUUID,
    val name: String
) : RabbitRequestPacket<NullableTrophyPlayerResponsePacket>()