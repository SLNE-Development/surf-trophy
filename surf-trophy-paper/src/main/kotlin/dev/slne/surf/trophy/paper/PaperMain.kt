package dev.slne.surf.trophy.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.surfapi.bukkit.api.event.register
import dev.slne.surf.trophy.core.common.service.trophyService
import dev.slne.surf.trophy.core.paper.PaperTrophyInstance
import dev.slne.surf.trophy.paper.command.trophyCommand
import dev.slne.surf.trophy.paper.listener.PlayerConnectionListener
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override suspend fun onLoadAsync() {
        PaperTrophyInstance.paperLoader.onLoad()
    }

    override suspend fun onEnableAsync() {
        PaperTrophyInstance.paperLoader.onEnable()

        PlayerConnectionListener.register()
        trophyCommand()

        trophyService.refreshTrophies()
    }

    override suspend fun onDisableAsync() {
        PaperTrophyInstance.paperLoader.onDisable()
    }
}