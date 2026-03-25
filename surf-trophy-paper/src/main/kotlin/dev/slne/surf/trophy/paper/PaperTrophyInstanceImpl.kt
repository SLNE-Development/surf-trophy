package dev.slne.surf.trophy.paper

import com.google.auto.service.AutoService
import dev.slne.surf.trophy.core.common.TrophyInstance
import dev.slne.surf.trophy.core.paper.PaperLoader
import dev.slne.surf.trophy.core.paper.PaperTrophyInstance
import net.kyori.adventure.util.Services

@AutoService(TrophyInstance::class)
class PaperTrophyInstanceImpl : PaperTrophyInstance, Services.Fallback {
    override val paperLoader = PaperLoader(plugin.dataPath)
}