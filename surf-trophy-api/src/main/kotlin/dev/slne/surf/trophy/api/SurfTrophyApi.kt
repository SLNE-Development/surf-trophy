package dev.slne.surf.trophy.api

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.trophy.api.trophy.ReceivedTrophy
import java.util.*

@Deprecated("This API is deprecated and will be removed in a future release. Please use the companion object instead!")
val surfTrophyApi = requiredService<SurfTrophyApi>()

interface SurfTrophyApi {
    fun showTrophyMenu(target: UUID, viewer: UUID)
    suspend fun getOrLoadTrophies(targetUuid: UUID): List<ReceivedTrophy>

    companion object : SurfTrophyApi by surfTrophyApi
}