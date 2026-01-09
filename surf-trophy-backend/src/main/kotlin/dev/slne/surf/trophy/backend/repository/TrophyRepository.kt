package dev.slne.surf.trophy.backend.repository

import dev.slne.surf.trophy.api.Trophy
import java.util.*

val trophyRepository = TrophyRepository()

class TrophyRepository {
    suspend fun refreshTrophies()

    suspend fun loadTrophyByUuid(uuid: UUID): Trophy?
    suspend fun saveTrophy(trophy: Trophy)

    suspend fun loadTrophies()
}