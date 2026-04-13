package dev.slne.surf.trophy.core.paper.service

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.service.PlayerLookupService
import dev.slne.surf.api.core.util.mutableObject2ObjectMapOf
import dev.slne.surf.trophy.api.player.TrophyPlayer
import dev.slne.surf.trophy.api.trophy.ReceivedTrophy
import dev.slne.surf.trophy.api.trophy.Trophy
import dev.slne.surf.trophy.core.common.rabbit.packet.request.player.GiveTrophyRequestPacket
import dev.slne.surf.trophy.core.common.rabbit.packet.request.player.LoadTrophyPlayerRequestPacket
import dev.slne.surf.trophy.core.common.rabbit.packet.request.player.SaveTrophyPlayerRequestPacket
import dev.slne.surf.trophy.core.common.rabbit.packet.request.player.TakeTrophyRequestPacket
import dev.slne.surf.trophy.core.common.service.PlayerTrophyService
import dev.slne.surf.trophy.core.paper.PaperTrophyInstance
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(PlayerTrophyService::class)
class PlayerTrophyServiceImpl : PlayerTrophyService, Services.Fallback {
    private val _players = mutableObject2ObjectMapOf<UUID, TrophyPlayer>()

    override fun findPlayerByUuid(uuid: UUID) = _players[uuid]
    override fun findPlayerByName(name: String) = _players.values.find { it.name == name }

    override fun cachePlayer(trophyPlayer: TrophyPlayer) {
        _players[trophyPlayer.uuid] = trophyPlayer
    }

    override fun invalidatePlayer(playerUuid: UUID) {
        _players.remove(playerUuid)
    }

    override suspend fun loadPlayerByUuid(uuid: UUID): TrophyPlayer? {
        val name = PlayerLookupService.getUsername(uuid) ?: return null
        val response = PaperTrophyInstance.paperLoader.rabbitApi.sendRequest(
            LoadTrophyPlayerRequestPacket(uuid, name)
        )

        return response.trophyPlayer
    }

    private suspend fun loadPlayerByUuidAndName(uuid: UUID, name: String): TrophyPlayer? =
        PaperTrophyInstance.paperLoader.rabbitApi.sendRequest(
            LoadTrophyPlayerRequestPacket(uuid, name)
        ).trophyPlayer

    override suspend fun loadPlayerByName(name: String): TrophyPlayer? {
        val uuid = PlayerLookupService.getUuid(name) ?: return null

        return loadPlayerByUuid(uuid)
    }

    override suspend fun giveTrophy(
        player: TrophyPlayer,
        trophy: Trophy
    ): Boolean {
        val receiveTrophy = ReceivedTrophy(trophy, System.currentTimeMillis())
        player.trophies.add(receiveTrophy)

        PlayerTrophyService.cachePlayer(player)

        return PaperTrophyInstance.paperLoader.rabbitApi.sendRequest(
            GiveTrophyRequestPacket(
                player.uuid,
                receiveTrophy
            )
        ).value
    }

    override suspend fun takeTrophy(
        player: TrophyPlayer,
        trophy: Trophy
    ): Boolean {
        player.trophies.removeIf { it.trophy.uuid == trophy.uuid }

        PlayerTrophyService.cachePlayer(player)
        return PaperTrophyInstance.paperLoader.rabbitApi.sendRequest(
            TakeTrophyRequestPacket(
                player.uuid,
                trophy
            )
        ).value
    }

    override suspend fun loadOrGetPlayerByName(name: String) =
        _players.values.find { it.name == name } ?: loadPlayerByName(name)

    override suspend fun loadOrGetPlayerByUuid(uuid: UUID) =
        _players.values.find { it.uuid == uuid } ?: loadPlayerByUuid(uuid)

    private suspend fun loadOrGetPlayerByUuidAndName(uuid: UUID, name: String) =
        _players.values.find { it.uuid == uuid } ?: loadPlayerByUuidAndName(uuid, name)

    override suspend fun loadOrGetOrCreatePlayerByUuidAndName(
        uuid: UUID,
        name: String
    ) = loadOrGetPlayerByUuidAndName(uuid, name)
        ?: PaperTrophyInstance.paperLoader.rabbitApi.sendRequest(
            SaveTrophyPlayerRequestPacket(
                TrophyPlayer(uuid, name, mutableListOf(), null)
            )
        ).trophyPlayer

    override suspend fun savePlayer(trophyPlayer: TrophyPlayer) =
        PaperTrophyInstance.paperLoader.rabbitApi.sendRequest(
            SaveTrophyPlayerRequestPacket(trophyPlayer)
        ).trophyPlayer
}