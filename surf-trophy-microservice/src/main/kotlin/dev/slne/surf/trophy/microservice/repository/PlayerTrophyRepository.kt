package dev.slne.surf.trophy.microservice.repository

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.ResultRow
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.and
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.deleteWhere
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.upsert
import dev.slne.surf.trophy.api.player.TrophyPlayer
import dev.slne.surf.trophy.api.trophy.ReceivedTrophy
import dev.slne.surf.trophy.api.trophy.Trophy
import dev.slne.surf.trophy.microservice.table.PlayerTrophiesTable
import dev.slne.surf.trophy.microservice.table.SelectedTrophyTable
import dev.slne.surf.trophy.microservice.table.TrophiesTable
import glm_.value
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import java.util.*

object PlayerTrophyRepository {
    suspend fun loadPlayerByUuid(uuid: UUID, name: String): TrophyPlayer = suspendTransaction {
        createPlayer(
            uuid, name,
            loadReceivedTrophies(uuid),
            loadSelectedTrophy(uuid)
        )
    }

    suspend fun giveTrophy(playerUuid: UUID, trophy: ReceivedTrophy): Boolean =
        suspendTransaction {
            val trophyRow = TrophiesTable
                .selectAll()
                .where(TrophiesTable.uuid eq trophy.trophy.uuid)
                .firstOrNull()
                ?: return@suspendTransaction false

            val trophyId = trophyRow[TrophiesTable.id].value

            PlayerTrophiesTable.insert {
                it[this.playerUuid] = playerUuid
                it[this.trophyId] = trophyId
                it[this.receivedAt] = trophy.receivedAt
            }

            true
        }

    suspend fun takeTrophy(playerUuid: UUID, trophy: Trophy): Boolean =
        suspendTransaction {
            val trophyRow = TrophiesTable
                .selectAll()
                .where(TrophiesTable.uuid eq trophy.uuid)
                .firstOrNull()
                ?: return@suspendTransaction false

            val trophyId = trophyRow[TrophiesTable.id].value

            val deletedRows = PlayerTrophiesTable.deleteWhere {
                (PlayerTrophiesTable.playerUuid eq playerUuid) and
                        (PlayerTrophiesTable.trophyId eq trophyId)
            }

            deletedRows > 0
        }

    suspend fun savePlayer(trophyPlayer: TrophyPlayer) = suspendTransaction {
        PlayerTrophiesTable.deleteWhere { PlayerTrophiesTable.playerUuid eq trophyPlayer.uuid }

        trophyPlayer.trophies.forEach { received ->
            val trophyId =
                TrophiesTable.selectAll().where(TrophiesTable.uuid eq received.trophy.uuid)
                    .firstOrNull()?.get(TrophiesTable.id)?.value ?: return@forEach

            PlayerTrophiesTable.insert {
                it[this.playerUuid] = trophyPlayer.uuid
                it[this.trophyId] = trophyId
                it[receivedAt] = received.receivedAt
            }
        }

        SelectedTrophyTable.deleteWhere { SelectedTrophyTable.playerUuid eq trophyPlayer.uuid }
        trophyPlayer.selectedTrophy?.let { selectedTrophy ->
            val trophyId =
                TrophiesTable.selectAll().where(TrophiesTable.uuid eq selectedTrophy.trophy.uuid)
                    .firstOrNull()?.get(TrophiesTable.id)?.value ?: return@let

            SelectedTrophyTable.upsert {
                it[this.playerUuid] = trophyPlayer.uuid
                it[this.trophyId] = trophyId
            }
        }
        trophyPlayer
    }

    private suspend fun loadReceivedTrophies(playerUuid: UUID): MutableList<ReceivedTrophy> =
        suspendTransaction {
            (PlayerTrophiesTable innerJoin TrophiesTable)
                .selectAll()
                .where(PlayerTrophiesTable.playerUuid eq playerUuid)
                .map { row ->
                    val trophy = TrophyRepository.createTrophyFromRow(row)
                    createReceivedTrophyFromRow(row, trophy)
                }.toList().toMutableList()
        }

    private suspend fun loadSelectedTrophy(playerUuid: UUID): ReceivedTrophy? =
        suspendTransaction {
            val trophyId =
                SelectedTrophyTable.selectAll().where(SelectedTrophyTable.playerUuid eq playerUuid)
                    .firstOrNull()?.get(SelectedTrophyTable.trophyId)?.value
                    ?: return@suspendTransaction null

            val trophy = TrophiesTable.selectAll().where(TrophiesTable.id eq trophyId).firstOrNull()
                ?.let { trophyRow ->
                    TrophyRepository.createTrophyFromRow(trophyRow)
                } ?: return@suspendTransaction null

            (PlayerTrophiesTable innerJoin TrophiesTable)
                .selectAll()
                .where(
                    (PlayerTrophiesTable.playerUuid eq playerUuid) and
                            (PlayerTrophiesTable.trophyId eq trophyId)
                ).firstOrNull()?.let {
                    createReceivedTrophyFromRow(it, trophy)
                }

        }

    private fun createPlayer(
        uuid: UUID,
        name: String,
        trophies: MutableList<ReceivedTrophy>,
        selectedTrophy: ReceivedTrophy?
    ): TrophyPlayer =
        TrophyPlayer(
            uuid,
            name,
            trophies,
            selectedTrophy
        )

    private fun createReceivedTrophyFromRow(
        row: ResultRow,
        trophy: Trophy
    ): ReceivedTrophy =
        ReceivedTrophy(
            trophy,
            row[PlayerTrophiesTable.receivedAt]
        )
}
