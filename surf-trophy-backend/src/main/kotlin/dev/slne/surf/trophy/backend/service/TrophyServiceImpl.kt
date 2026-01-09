package dev.slne.surf.trophy.backend.service

import com.google.auto.service.AutoService
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import dev.slne.surf.trophy.api.trophy.Trophy
import dev.slne.surf.trophy.backend.repository.trophyRepository
import dev.slne.surf.trophy.core.service.TrophyService
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(TrophyService::class)
class TrophyServiceImpl : TrophyService, Services.Fallback {
    private val trophies = mutableObjectSetOf<Trophy>()

    override fun findTrophyByName(name: String) = trophies.find { it.name == name }
    override fun findTrophyByUuid(uuid: UUID) = trophies.find { it.uuid == uuid }
    override fun getTrophies() = trophies.toObjectSet()

    override fun cacheTrophy(trophy: Trophy) {
        trophies.add(trophy)
    }

    override fun invalidateTrophy(trophy: Trophy) {
        trophies.remove(trophy)
    }

    override suspend fun refreshTrophies() {
        trophies.clear()
        trophies.addAll(loadTrophies())
    }

    override suspend fun loadTrophyByUuid(uuid: UUID) = trophyRepository.loadTrophyByUuid(uuid)
    override suspend fun saveTrophy(trophy: Trophy) = trophyRepository.saveTrophy(trophy)
    override suspend fun deleteTrophy(trophy: Trophy) {
        invalidateTrophy(trophy)
        trophyRepository.deleteTrophy(trophy)
    }

    override suspend fun loadTrophies() = trophyRepository.loadTrophies()
}