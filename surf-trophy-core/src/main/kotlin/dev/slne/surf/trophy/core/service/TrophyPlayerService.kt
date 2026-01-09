package dev.slne.surf.trophy.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import dev.slne.surf.trophy.api.TrophyPlayer
import java.util.*

val trophyPlayerService = requiredService<TrophyPlayerService>()

interface TrophyPlayerService {
    fun findPlayerByUuid(uuid: UUID): TrophyPlayer?
    fun findPlayerByName(name: String): TrophyPlayer?

    fun cachePlayer(trophyPlayer: TrophyPlayer)

    suspend fun loadPlayerByUuid(uuid: UUID): TrophyPlayer?
    suspend fun loadPlayerByName(name: String): TrophyPlayer?

    suspend fun loadOrGetPlayerByName(name: String): TrophyPlayer?
    suspend fun loadOrGetOrCreatePlayerByUuidAndName(uuid: UUID, name: String): TrophyPlayer

    suspend fun savePlayer(trophyPlayer: TrophyPlayer)
}