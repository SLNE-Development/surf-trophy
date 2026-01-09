package dev.slne.surf.trophy.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import dev.slne.surf.trophy.api.Trophy
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

val trophyService = requiredService<TrophyService>()

interface TrophyService {
    fun findTrophyByName(name: String): Trophy?
    fun findTrophyByUuid(uuid: UUID): Trophy?
    fun getTrophies(): ObjectSet<Trophy>

    fun cacheTrophy(trophy: Trophy)

    suspend fun refreshTrophies()

    suspend fun loadTrophyByUuid(uuid: UUID): Trophy?
    suspend fun saveTrophy(trophy: Trophy)

    suspend fun loadTrophies(): ObjectSet<Trophy>
}