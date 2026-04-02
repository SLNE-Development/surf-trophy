package dev.slne.surf.trophy.microservice.rabbit.handler

import dev.slne.surf.rabbitmq.api.handler.RabbitHandler
import dev.slne.surf.rabbitmq.api.packet.standard.response.primitive.PrimitiveResponse
import dev.slne.surf.trophy.core.common.rabbit.packet.request.trophy.DeleteTrophyRequestPacket
import dev.slne.surf.trophy.core.common.rabbit.packet.request.trophy.LoadTrophiesRequestPacket
import dev.slne.surf.trophy.core.common.rabbit.packet.request.trophy.SaveTrophyRequestPacket
import dev.slne.surf.trophy.core.common.rabbit.packet.response.ManyTrophyResponsePacket
import dev.slne.surf.trophy.microservice.repository.TrophyRepository
import kotlinx.coroutines.launch

object TrophiesHandler {
    @RabbitHandler
    fun onLoadTrophiesPacket(packet: LoadTrophiesRequestPacket) = packet.launch {
        packet.respond(ManyTrophyResponsePacket(TrophyRepository.loadTrophies()))
    }

    @RabbitHandler
    fun onSaveTrophyPacket(packet: SaveTrophyRequestPacket) = packet.launch {
        packet.respond(PrimitiveResponse.BooleanResponsePacket(TrophyRepository.saveTrophy(packet.trophy)))
    }

    @RabbitHandler
    fun onDeleteTrophyPacket(packet: DeleteTrophyRequestPacket) = packet.launch {
        packet.respond(PrimitiveResponse.BooleanResponsePacket(TrophyRepository.deleteTrophy(packet.trophy)))
    }
}