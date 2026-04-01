package dev.slne.surf.trophy.microservice.repository

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.ResultRow
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.deleteWhere
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.upsert
import dev.slne.surf.trophy.api.trophy.Trophy
import dev.slne.surf.trophy.microservice.table.TrophiesTable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

object TrophyRepository {
    suspend fun loadTrophies(): List<Trophy> = suspendTransaction {
        TrophiesTable
            .selectAll()
            .map(::createTrophyFromRow).toList()
    }

    suspend fun saveTrophy(trophy: Trophy) = suspendTransaction {
        TrophiesTable.upsert {
            it[uuid] = trophy.uuid
            it[name] = trophy.name
            it[description] = trophy.description
            it[item] = trophy.itemString
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
            itemString = row[TrophiesTable.item]
        )
}
