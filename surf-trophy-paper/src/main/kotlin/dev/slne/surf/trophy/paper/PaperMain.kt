package dev.slne.surf.trophy.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.surfapi.bukkit.api.event.register
import dev.slne.surf.trophy.core.database.databaseLoader
import dev.slne.surf.trophy.core.service.trophyService
import dev.slne.surf.trophy.paper.command.trophyCommand
import dev.slne.surf.trophy.paper.listener.PlayerConnectionListener
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override fun onEnable() {
        PlayerConnectionListener.register()

        trophyCommand()

        plugin.launch {
            databaseLoader.connect(plugin.dataPath)
            trophyService.refreshTrophies()
        }
    }

    override fun onDisable() {
        databaseLoader.disconnect()
    }
}