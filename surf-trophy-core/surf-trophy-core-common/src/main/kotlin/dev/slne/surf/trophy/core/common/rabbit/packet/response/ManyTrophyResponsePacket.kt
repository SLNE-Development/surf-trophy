package dev.slne.surf.trophy.core.common.rabbit.packet.response

import dev.slne.surf.rabbitmq.api.packet.RabbitResponsePacket
import dev.slne.surf.trophy.api.trophy.Trophy
import kotlinx.serialization.Serializable

@Serializable
data class ManyTrophyResponsePacket(
    val trophies: List<Trophy>
) : RabbitResponsePacket()
