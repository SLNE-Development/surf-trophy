package dev.slne.surf.trophy.api

import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.*

val surfTrophyApi = requiredService<SurfTrophyApi>()

interface SurfTrophyApi {
    fun showTrophyMenu(target: UUID, viewer: UUID)
}