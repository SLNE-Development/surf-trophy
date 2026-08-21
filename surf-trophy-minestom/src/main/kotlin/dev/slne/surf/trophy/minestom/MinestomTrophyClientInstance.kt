package dev.slne.surf.trophy.minestom

import com.google.auto.service.AutoService
import dev.slne.surf.trophy.core.client.TrophyClientInstance
import dev.slne.surf.trophy.core.common.TrophyInstance
import net.kyori.adventure.util.Services
import java.nio.file.Path

@AutoService(TrophyInstance::class)
class MinestomTrophyClientInstance : TrophyClientInstance(), Services.Fallback {
    override val dataPath: Path get() = SurfTrophyMinestomEntrypoint.dataPath
}
