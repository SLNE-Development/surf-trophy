package dev.slne.surf.trophy.minestom

import com.google.auto.service.AutoService
import dev.slne.minestom.lobby.api.plugin.MinestomPlugin
import dev.slne.minestom.lobby.api.plugin.annotation.MinestomPluginMeta
import dev.slne.surf.trophy.minestom.command.TrophyCommandRegistrar
import dev.slne.surf.trophy.minestom.listener.ConnectionListener

@AutoService(MinestomPlugin::class)
@MinestomPluginMeta(
    "surf-trophy-minestom",
    dependsOn = [
        "surf-api-minestom",
        "surf-rabbitmq-minestom",
        "surf-core-minestom"
    ]
)
class SurfTrophyMinestomPlugin : MinestomPlugin(SurfTrophyMinestomEntrypoint::class.java) {
    override fun configurePlugin() {
        bindCommandRegistrar<TrophyCommandRegistrar>()
        bindEventRegistrar<ConnectionListener>()
    }
}
