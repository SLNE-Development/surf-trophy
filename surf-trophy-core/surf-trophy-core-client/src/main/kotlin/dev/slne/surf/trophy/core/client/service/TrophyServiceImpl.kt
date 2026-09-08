package dev.slne.surf.trophy.core.client.service

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.util.freeze
import dev.slne.surf.api.core.util.logger
import dev.slne.surf.api.core.util.mutableObject2ObjectMapOf
import dev.slne.surf.api.core.util.mutableObjectSetOf
import dev.slne.surf.trophy.api.trophy.Trophy
import dev.slne.surf.trophy.core.client.rabbitApi
import dev.slne.surf.trophy.core.common.rabbit.packet.request.trophy.DeleteTrophyRequestPacket
import dev.slne.surf.trophy.core.common.rabbit.packet.request.trophy.LoadTrophiesRequestPacket
import dev.slne.surf.trophy.core.common.rabbit.packet.request.trophy.SaveTrophyRequestPacket
import dev.slne.surf.trophy.core.common.service.TrophyService
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.util.Services
import java.util.*
import java.util.concurrent.atomic.AtomicReference

@AutoService(TrophyService::class)
class TrophyServiceImpl : TrophyService, Services.Fallback {
    private val log = logger()

    /**
     * Every cached trophy, held as one immutable snapshot.
     */
    private val snapshot = AtomicReference(TrophySnapshot.EMPTY)

    override fun findTrophyByName(name: String): Trophy? = snapshot.get().byName[name]
    override fun findTrophyByUuid(uuid: UUID): Trophy? = snapshot.get().byUuid[uuid]
    override fun getTrophies(): ObjectSet<Trophy> = snapshot.get().trophies

    override fun cacheTrophy(trophy: Trophy) {
        snapshot.updateAndGet { current -> current.with(trophy) }
    }

    override fun invalidateTrophy(trophy: Trophy) {
        snapshot.updateAndGet { current -> current.without(trophy) }
    }

    override suspend fun refreshTrophies() {
        val refreshed = TrophySnapshot.of(loadTrophies())
        snapshot.set(refreshed)

        log.atInfo().log("Trophy cache refreshed, total trophies: ${refreshed.trophies.size}")
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

/**
 * An immutable set of trophies together with the indexes the lookups read.
 */
private class TrophySnapshot private constructor(
    val trophies: ObjectSet<Trophy>,
    val byName: Object2ObjectMap<String, Trophy>,
    val byUuid: Object2ObjectMap<UUID, Trophy>,
) {

    /** This snapshot including [trophy], or itself when the trophy is already cached. */
    fun with(trophy: Trophy): TrophySnapshot {
        return if (trophies.contains(trophy)) {
            this
        } else {
            of(mutableObjectSetOf(trophies).also { it.add(trophy) })
        }
    }

    /** This snapshot without [trophy], or itself when the trophy is not cached. */
    fun without(trophy: Trophy): TrophySnapshot {
        return if (!trophies.contains(trophy)) {
            this
        } else {
            of(mutableObjectSetOf(trophies).also { it.remove(trophy) })
        }
    }

    companion object {
        val EMPTY = of(emptyList())

        fun of(trophies: Collection<Trophy>): TrophySnapshot {
            val set = mutableObjectSetOf(trophies)
            val byName = mutableObject2ObjectMapOf<String, Trophy>(set.size)
            val byUuid = mutableObject2ObjectMapOf<UUID, Trophy>(set.size)

            for (trophy in trophies) {
                byName.putIfAbsent(trophy.name, trophy)
                byUuid.putIfAbsent(trophy.uuid, trophy)
            }

            return TrophySnapshot(set.freeze(), byName, byUuid)
        }
    }
}
