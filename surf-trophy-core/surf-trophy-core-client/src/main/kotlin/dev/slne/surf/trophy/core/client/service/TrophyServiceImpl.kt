package dev.slne.surf.trophy.core.client.service

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.util.logger
import dev.slne.surf.api.core.util.mutableObjectSetOf
import dev.slne.surf.api.core.util.toObjectSet
import dev.slne.surf.trophy.api.trophy.Trophy
import dev.slne.surf.trophy.core.client.rabbitApi
import dev.slne.surf.trophy.core.common.rabbit.packet.request.trophy.DeleteTrophyRequestPacket
import dev.slne.surf.trophy.core.common.rabbit.packet.request.trophy.LoadTrophiesRequestPacket
import dev.slne.surf.trophy.core.common.rabbit.packet.request.trophy.SaveTrophyRequestPacket
import dev.slne.surf.trophy.core.common.service.TrophyService
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(TrophyService::class)
class TrophyServiceImpl : TrophyService, Services.Fallback {
    private val log = logger()
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

        log.atInfo().log("Trophy cache refreshed, total trophies: ${trophies.size}")
    }

    override suspend fun saveTrophy(trophy: Trophy) = rabbitApi.sendRequest(
        SaveTrophyRequestPacket(
            trophy
        )
    ).value

    override suspend fun deleteTrophy(trophy: Trophy): Boolean {
        invalidateTrophy(trophy)
        return rabbitApi.sendRequest(
            DeleteTrophyRequestPacket(
                trophy
            )
        ).value
    }

    override suspend fun loadTrophies() = rabbitApi.sendRequest(
        LoadTrophiesRequestPacket()
    ).trophies
}
