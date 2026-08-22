package dev.slne.surf.trophy.core.client.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.auto.service.AutoService
import dev.slne.surf.api.core.service.PlayerLookupService
import dev.slne.surf.trophy.api.player.TrophyPlayer
import dev.slne.surf.trophy.api.trophy.ReceivedTrophy
import dev.slne.surf.trophy.api.trophy.Trophy
import dev.slne.surf.trophy.core.client.rabbitApi
import dev.slne.surf.trophy.core.common.rabbit.packet.request.player.*
import dev.slne.surf.trophy.core.common.service.PlayerTrophyService
import java.util.*

@AutoService(PlayerTrophyService::class)
class PlayerTrophyServiceImpl : PlayerTrophyService {
    private val playerCache = Caffeine.newBuilder()
        .build<UUID, TrophyPlayer>()

    override fun findPlayerByUuid(uuid: UUID) = playerCache.getIfPresent(uuid)
    override fun findPlayerByName(name: String) = playerCache
        .asMap()
        .values
        .find { it.name == name }

    override fun cachePlayer(trophyPlayer: TrophyPlayer) {
        playerCache.put(trophyPlayer.uuid, trophyPlayer)
    }

    override fun invalidatePlayer(playerUuid: UUID) {
        playerCache.invalidate(playerUuid)
    }

    override suspend fun loadPlayerByUuid(uuid: UUID): TrophyPlayer? {
        val name = PlayerLookupService.getUsername(uuid) ?: return null

        return loadPlayerByUuidAndName(uuid, name)
    }

    private suspend fun loadPlayerByUuidAndName(uuid: UUID, name: String): TrophyPlayer? =
        rabbitApi.sendRequest(
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

        cachePlayer(player)

        return rabbitApi.sendRequest(
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

        cachePlayer(player)
        return rabbitApi.sendRequest(
            TakeTrophyRequestPacket(
                player.uuid,
                trophy
            )
        ).value
    }

    override suspend fun loadOrGetPlayerByName(name: String) =
        findPlayerByName(name) ?: loadPlayerByName(name)

    override suspend fun loadOrGetPlayerByUuid(uuid: UUID) =
        findPlayerByUuid(uuid) ?: loadPlayerByUuid(uuid)

    private suspend fun loadOrGetPlayerByUuidAndName(uuid: UUID, name: String) =
        findPlayerByUuid(uuid) ?: loadPlayerByUuidAndName(uuid, name)

    override suspend fun loadOrGetOrCreatePlayerByUuidAndName(
        uuid: UUID,
        name: String
    ) = loadOrGetPlayerByUuidAndName(uuid, name)
        ?: rabbitApi.sendRequest(
            SaveTrophyPlayerRequestPacket(
                TrophyPlayer(uuid, name, mutableListOf(), null)
            )
        ).trophyPlayer

    override suspend fun savePlayer(trophyPlayer: TrophyPlayer) = rabbitApi.sendRequest(
        SaveTrophyPlayerRequestPacket(trophyPlayer)
    ).trophyPlayer

    override suspend fun saveSelectedTrophy(trophyPlayer: TrophyPlayer): Boolean {
        cachePlayer(trophyPlayer)

        return rabbitApi.sendRequest(
            SaveSelectedTrophyRequestPacket(
                trophyPlayer.uuid,
                trophyPlayer.selectedTrophy?.trophy
            )
        ).value
    }
}
