package dev.slne.surf.trophy.backend.repository

import dev.slne.surf.trophy.api.TrophyPlayer
import java.util.*

val trophyPlayerRepository = TrophyPlayerRepository()

class TrophyPlayerRepository {
    suspend fun loadPlayerByUuid(uuid: UUID): TrophyPlayer?
    suspend fun loadPlayerByName(name: String): TrophyPlayer?

    suspend fun loadOrGetPlayerByName(name: String): TrophyPlayer?
    suspend fun loadOrGetOrCreatePlayerByUuidAndName(uuid: UUID, name: String): TrophyPlayer

    suspend fun savePlayer(trophyPlayer: TrophyPlayer)
}