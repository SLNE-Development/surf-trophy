package dev.slne.surf.trophy.backend.service

import com.google.auto.service.AutoService
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.trophy.api.player.TrophyPlayer
import dev.slne.surf.trophy.api.trophy.ReceivedTrophy
import dev.slne.surf.trophy.api.trophy.Trophy
import dev.slne.surf.trophy.backend.repository.trophyPlayerRepository
import dev.slne.surf.trophy.core.service.TrophyPlayerService
import dev.slne.surf.trophy.core.service.trophyPlayerService
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(TrophyPlayerService::class)
class TrophyPlayerServiceImpl : TrophyPlayerService, Services.Fallback {
    private val players = mutableObject2ObjectMapOf<UUID, TrophyPlayer>()

    override fun findPlayerByUuid(uuid: UUID) = players[uuid]

    override fun findPlayerByName(name: String) = players.values.find { it.name == name }

    override fun cachePlayer(trophyPlayer: TrophyPlayer) {
        players[trophyPlayer.uuid] = trophyPlayer
    }

    override fun invalidatePlayer(playerUuid: UUID) {
        players.remove(playerUuid)
    }

    override suspend fun loadPlayerByUuid(uuid: UUID) =
        trophyPlayerRepository.loadPlayerByUuid(uuid)

    override suspend fun loadPlayerByName(name: String) = trophyPlayerService.loadPlayerByName(name)
    override suspend fun giveTrophy(
        player: TrophyPlayer,
        trophy: Trophy
    ): Boolean {
        val receiveTrophy = ReceivedTrophy(trophy, System.currentTimeMillis())
        player.trophies.add(receiveTrophy)

        trophyPlayerService.cachePlayer(player)
        return trophyPlayerRepository.giveTrophy(player, receiveTrophy)
    }

    override suspend fun takeTrophy(
        player: TrophyPlayer,
        trophy: Trophy
    ): Boolean {
        player.trophies.removeIf { it.trophy.uuid == trophy.uuid }

        trophyPlayerService.cachePlayer(player)
        return trophyPlayerRepository.takeTrophy(player, trophy)
    }

    override suspend fun loadOrGetPlayerByName(name: String) =
        players.values.find { it.name == name } ?: loadPlayerByName(name)

    override suspend fun loadOrGetPlayerByUuid(uuid: UUID) =
        players.values.find { it.uuid == uuid } ?: loadPlayerByUuid(uuid)

    override suspend fun loadOrGetOrCreatePlayerByUuidAndName(
        uuid: UUID,
        name: String
    ) = trophyPlayerRepository.loadOrGetOrCreatePlayerByUuidAndName(uuid, name)

    override suspend fun savePlayer(trophyPlayer: TrophyPlayer) =
        trophyPlayerRepository.savePlayer(trophyPlayer)
}