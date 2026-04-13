package dev.slne.surf.trophy.core.common.service

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.trophy.api.trophy.Trophy
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

private val service = requiredService<TrophyService>()

interface TrophyService {
    fun findTrophyByName(name: String): Trophy?
    fun findTrophyByUuid(uuid: UUID): Trophy?
    fun getTrophies(): ObjectSet<Trophy>

    fun cacheTrophy(trophy: Trophy)
    fun invalidateTrophy(trophy: Trophy)

    suspend fun refreshTrophies()

    suspend fun saveTrophy(trophy: Trophy): Boolean
    suspend fun deleteTrophy(trophy: Trophy): Boolean

    suspend fun loadTrophies(): List<Trophy>

    companion object : TrophyService by service {
        val INSTANCE get() = service
    }
}