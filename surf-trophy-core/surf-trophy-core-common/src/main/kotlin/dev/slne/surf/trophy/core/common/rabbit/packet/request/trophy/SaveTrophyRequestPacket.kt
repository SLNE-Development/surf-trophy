package dev.slne.surf.trophy.core.common.rabbit.packet.request.trophy

import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import dev.slne.surf.rabbitmq.api.packet.standard.response.primitive.PrimitiveResponse
import dev.slne.surf.trophy.api.trophy.Trophy
import kotlinx.serialization.Serializable

@Serializable
data class SaveTrophyRequestPacket(
    val trophy: Trophy
) : RabbitRequestPacket<PrimitiveResponse.BooleanResponsePacket>()
