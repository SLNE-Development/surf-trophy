package dev.slne.surf.trophy.backend.repository

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.ResultRow
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.deleteWhere
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.upsert
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import dev.slne.surf.trophy.api.trophy.Trophy
import dev.slne.surf.trophy.backend.table.TrophiesTable
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import java.util.*

val trophyRepository = TrophyRepository()

class TrophyRepository {
    suspend fun loadTrophyByUuid(uuid: UUID): Trophy? = suspendTransaction {
        TrophiesTable
            .selectAll()
            .where(TrophiesTable.uuid eq uuid)
            .firstOrNull()
            ?.let(::createTrophyFromRow)
    }

    suspend fun loadTrophies(): ObjectSet<Trophy> = suspendTransaction {
        TrophiesTable
            .selectAll()
            .map(::createTrophyFromRow).toList().toObjectSet()
    }

    suspend fun saveTrophy(trophy: Trophy) = suspendTransaction {
        TrophiesTable.upsert {
            it[uuid] = trophy.uuid
            it[name] = trophy.name
            it[description] = trophy.description
            it[item] = trophy.item
        }

        Unit
    }

    suspend fun deleteTrophy(trophy: Trophy) = suspendTransaction {
        TrophiesTable.deleteWhere {
            TrophiesTable.uuid eq trophy.uuid
        }
        Unit
    }

    fun createTrophyFromRow(row: ResultRow): Trophy =
        Trophy(
            uuid = row[TrophiesTable.uuid],
            name = row[TrophiesTable.name],
            description = row[TrophiesTable.description],
            item = row[TrophiesTable.item]
        )
}
