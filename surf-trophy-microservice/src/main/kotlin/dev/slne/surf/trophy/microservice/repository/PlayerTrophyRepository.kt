package dev.slne.surf.trophy.microservice.repository

import dev.slne.surf.api.core.util.mutableLongListOf
import dev.slne.surf.api.core.util.mutableObjectListOf
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.ResultRow
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.and
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.*
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.trophy.api.player.TrophyPlayer
import dev.slne.surf.trophy.api.trophy.ReceivedTrophy
import dev.slne.surf.trophy.api.trophy.Trophy
import dev.slne.surf.trophy.microservice.table.PlayerTrophiesTable
import dev.slne.surf.trophy.microservice.table.SelectedTrophyTable
import dev.slne.surf.trophy.microservice.table.TrophiesTable
import kotlinx.coroutines.flow.firstOrNull
import java.util.*

object PlayerTrophyRepository {

    suspend fun loadPlayerByUuid(uuid: UUID, name: String): TrophyPlayer = suspendTransaction {
        val trophies = mutableObjectListOf<ReceivedTrophy>()
        val trophyIds = mutableLongListOf()

        (PlayerTrophiesTable innerJoin TrophiesTable)
            .selectAll()
            .where(PlayerTrophiesTable.playerUuid eq uuid)
            .collect { row ->
                trophies.add(
                    createReceivedTrophyFromRow(row, TrophyRepository.createTrophyFromRow(row))
                )
                trophyIds.add(row[PlayerTrophiesTable.trophyId])
            }

        val selectedTrophyId = SelectedTrophyTable
            .select(SelectedTrophyTable.trophyId)
            .where(SelectedTrophyTable.playerUuid eq uuid)
            .firstOrNull()
            ?.get(SelectedTrophyTable.trophyId)

        val selectedIndex =
            if (selectedTrophyId == null) -1 else trophyIds.indexOf(selectedTrophyId)

        TrophyPlayer(uuid, name, trophies, trophies.getOrNull(selectedIndex))
    }

    suspend fun giveTrophy(playerUuid: UUID, trophy: ReceivedTrophy): Boolean =
        suspendTransaction {
            val trophyId = trophyIdOf(trophy.trophy.uuid) ?: return@suspendTransaction false

            PlayerTrophiesTable.insert {
                it[this.playerUuid] = playerUuid
                it[this.trophyId] = trophyId
                it[this.receivedAt] = trophy.receivedAt
            }

            true
        }

    suspend fun takeTrophy(playerUuid: UUID, trophy: Trophy): Boolean =
        suspendTransaction {
            val trophyId = trophyIdOf(trophy.uuid) ?: return@suspendTransaction false

            val deletedRows = PlayerTrophiesTable.deleteWhere {
                (PlayerTrophiesTable.playerUuid eq playerUuid) and
                        (PlayerTrophiesTable.trophyId eq trophyId)
            }

            deletedRows > 0
        }

    suspend fun saveSelectedTrophy(playerUuid: UUID, trophy: Trophy?): Boolean =
        suspendTransaction {
            SelectedTrophyTable.deleteWhere { SelectedTrophyTable.playerUuid eq playerUuid }

            if (trophy == null) return@suspendTransaction true

            val trophyId = trophyIdOf(trophy.uuid) ?: return@suspendTransaction false

            SelectedTrophyTable.insert {
                it[this.playerUuid] = playerUuid
                it[this.trophyId] = trophyId
            }

            true
        }

    suspend fun savePlayer(trophyPlayer: TrophyPlayer) = suspendTransaction {
        PlayerTrophiesTable.deleteWhere { PlayerTrophiesTable.playerUuid eq trophyPlayer.uuid }

        trophyPlayer.trophies.forEach { received ->
            val trophyId = trophyIdOf(received.trophy.uuid) ?: return@forEach

            PlayerTrophiesTable.insert {
                it[this.playerUuid] = trophyPlayer.uuid
                it[this.trophyId] = trophyId
                it[receivedAt] = received.receivedAt
            }
        }

        SelectedTrophyTable.deleteWhere { SelectedTrophyTable.playerUuid eq trophyPlayer.uuid }
        trophyPlayer.selectedTrophy?.let { selectedTrophy ->
            val trophyId = trophyIdOf(selectedTrophy.trophy.uuid) ?: return@let

            SelectedTrophyTable.upsert {
                it[this.playerUuid] = trophyPlayer.uuid
                it[this.trophyId] = trophyId
            }
        }
        trophyPlayer
    }

    /**
     * The row id of the trophy identified by [trophyUuid], or `null` when no such trophy is
     * stored.
     */
    private suspend fun trophyIdOf(trophyUuid: UUID): Long? = TrophiesTable
        .select(TrophiesTable.id)
        .where(TrophiesTable.uuid eq trophyUuid)
        .firstOrNull()
        ?.get(TrophiesTable.id)
        ?.value

    private fun createReceivedTrophyFromRow(
        row: ResultRow,
        trophy: Trophy
    ): ReceivedTrophy =
        ReceivedTrophy(
            trophy,
            row[PlayerTrophiesTable.receivedAt]
        )
}
