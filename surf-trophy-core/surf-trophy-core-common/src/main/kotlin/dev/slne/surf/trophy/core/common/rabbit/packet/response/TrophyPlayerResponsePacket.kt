package dev.slne.surf.trophy.core.common.rabbit.packet.response

import dev.slne.surf.rabbitmq.api.packet.RabbitResponsePacket
import dev.slne.surf.trophy.api.player.TrophyPlayer
import kotlinx.serialization.Serializable

@Serializable
data class TrophyPlayerResponsePacket(
    val trophyPlayer: TrophyPlayer
) : RabbitResponsePacket()
