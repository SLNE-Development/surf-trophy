package dev.slne.surf.trophy.microservice.rabbit.handler

import dev.slne.surf.rabbitmq.api.handler.RabbitHandler
import dev.slne.surf.rabbitmq.api.packet.standard.response.primitive.PrimitiveResponse
import dev.slne.surf.trophy.core.common.rabbit.packet.request.player.GiveTrophyRequestPacket
import dev.slne.surf.trophy.core.common.rabbit.packet.request.player.LoadTrophyPlayerRequestPacket
import dev.slne.surf.trophy.core.common.rabbit.packet.request.player.SaveTrophyPlayerRequestPacket
import dev.slne.surf.trophy.core.common.rabbit.packet.request.player.TakeTrophyRequestPacket
import dev.slne.surf.trophy.core.common.rabbit.packet.response.NullableTrophyPlayerResponsePacket
import dev.slne.surf.trophy.core.common.rabbit.packet.response.TrophyPlayerResponsePacket
import dev.slne.surf.trophy.microservice.repository.trophyPlayerRepository
import kotlinx.coroutines.launch

object PlayerTrophiesHandler {
    @RabbitHandler
    fun onGiveTrophyPacket(packet: GiveTrophyRequestPacket) = packet.launch {
        packet.respond(
            PrimitiveResponse.BooleanResponsePacket(
                trophyPlayerRepository.giveTrophy(
                    packet.playerUuid,
                    packet.receivedTrophy
                )
            )
        )
    }

    @RabbitHandler
    fun onLoadTrophyPlayerPacket(packet: LoadTrophyPlayerRequestPacket) = packet.launch {
        packet.respond(
            NullableTrophyPlayerResponsePacket(
                trophyPlayerRepository.loadPlayerByUuid(
                    packet.uuid,
                    packet.name
                )
            )
        )
    }

    @RabbitHandler
    fun onSaveTrophyPlayerPacket(packet: SaveTrophyPlayerRequestPacket) = packet.launch {
        packet.respond(
            TrophyPlayerResponsePacket(
                trophyPlayerRepository.savePlayer(packet.trophyPlayer)
            )
        )
    }

    @RabbitHandler
    fun onTakeTrophyPacket(packet: TakeTrophyRequestPacket) = packet.launch {
        packet.respond(
            PrimitiveResponse.BooleanResponsePacket(
                trophyPlayerRepository.takeTrophy(
                    packet.playerUuid,
                    packet.trophy
                )
            )
        )
    }

}