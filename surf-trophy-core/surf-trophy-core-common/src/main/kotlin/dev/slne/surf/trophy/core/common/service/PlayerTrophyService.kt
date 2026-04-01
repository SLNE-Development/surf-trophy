package dev.slne.surf.trophy.core.common.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import dev.slne.surf.trophy.api.player.TrophyPlayer
import dev.slne.surf.trophy.api.trophy.Trophy
import java.util.*

private val service = requiredService<PlayerTrophyService>()

interface PlayerTrophyService {
    fun findPlayerByUuid(uuid: UUID): TrophyPlayer?
    fun findPlayerByName(name: String): TrophyPlayer?

    fun cachePlayer(trophyPlayer: TrophyPlayer)
    fun invalidatePlayer(playerUuid: UUID)

    suspend fun loadPlayerByUuid(uuid: UUID): TrophyPlayer?
    suspend fun loadPlayerByName(name: String): TrophyPlayer?

    suspend fun giveTrophy(player: TrophyPlayer, trophy: Trophy): Boolean
    suspend fun takeTrophy(player: TrophyPlayer, trophy: Trophy): Boolean

    suspend fun loadOrGetPlayerByName(name: String): TrophyPlayer?
    suspend fun loadOrGetPlayerByUuid(uuid: UUID): TrophyPlayer?
    suspend fun loadOrGetOrCreatePlayerByUuidAndName(uuid: UUID, name: String): TrophyPlayer

    suspend fun savePlayer(trophyPlayer: TrophyPlayer): TrophyPlayer

    companion object : PlayerTrophyService by service {
        val INSTANCE get() = service
    }
}