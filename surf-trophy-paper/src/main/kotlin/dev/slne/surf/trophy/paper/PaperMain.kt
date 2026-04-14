package dev.slne.surf.trophy.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.api.paper.event.register
import dev.slne.surf.api.paper.inventory.framework.register
import dev.slne.surf.trophy.core.common.service.TrophyService
import dev.slne.surf.trophy.core.paper.PaperTrophyInstance
import dev.slne.surf.trophy.paper.command.trophyCommand
import dev.slne.surf.trophy.paper.listener.PlayerConnectionListener
import dev.slne.surf.trophy.paper.menu.noTrophiesMenu
import dev.slne.surf.trophy.paper.menu.otherTrophiesMenu
import dev.slne.surf.trophy.paper.menu.ownTrophiesMenu
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override suspend fun onLoadAsync() {
        PaperTrophyInstance.paperLoader.onLoad()

        noTrophiesMenu().register()
        otherTrophiesMenu().register()
        ownTrophiesMenu().register()
    }

    override suspend fun onEnableAsync() {
        PaperTrophyInstance.paperLoader.onEnable()

        PlayerConnectionListener.register()
        trophyCommand()

        TrophyService.refreshTrophies()
    }

    override suspend fun onDisableAsync() {
        PaperTrophyInstance.paperLoader.onDisable()
    }
}