package dev.slne.surf.trophy.core.common.rabbit.packet.request.trophy

import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import dev.slne.surf.trophy.core.common.rabbit.packet.response.ManyTrophyResponsePacket
import kotlinx.serialization.Serializable

@Serializable
class LoadTrophiesRequestPacket : RabbitRequestPacket<ManyTrophyResponsePacket>()
