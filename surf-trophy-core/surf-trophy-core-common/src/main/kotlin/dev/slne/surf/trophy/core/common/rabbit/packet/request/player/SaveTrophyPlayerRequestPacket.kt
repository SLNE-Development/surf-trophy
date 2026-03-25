package dev.slne.surf.trophy.core.common.rabbit.packet.request.player

import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import dev.slne.surf.trophy.api.player.TrophyPlayer
import dev.slne.surf.trophy.core.common.rabbit.packet.response.TrophyPlayerResponsePacket
import kotlinx.serialization.Serializable

@Serializable
data class SaveTrophyPlayerRequestPacket(
    val trophyPlayer: TrophyPlayer
) : RabbitRequestPacket<TrophyPlayerResponsePacket>()
