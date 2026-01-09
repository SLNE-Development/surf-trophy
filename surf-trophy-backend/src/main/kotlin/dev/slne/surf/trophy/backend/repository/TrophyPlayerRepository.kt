package dev.slne.surf.trophy.backend.repository

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.ResultRow
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.and
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.deleteWhere
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.surfapi.core.api.util.toMutableObjectList
import dev.slne.surf.trophy.api.player.TrophyPlayer
import dev.slne.surf.trophy.api.trophy.ReceivedTrophy
import dev.slne.surf.trophy.api.trophy.Trophy
import dev.slne.surf.trophy.backend.table.PlayerTrophiesTable
import dev.slne.surf.trophy.backend.table.TrophiesTable
import dev.slne.surf.trophy.backend.table.TrophyPlayerTable
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import java.util.*

val trophyPlayerRepository = TrophyPlayerRepository()

class TrophyPlayerRepository {

    suspend fun loadPlayerByUuid(uuid: UUID): TrophyPlayer? = suspendTransaction {
        TrophyPlayerTable
            .selectAll()
            .where(TrophyPlayerTable.uuid eq uuid)
            .firstOrNull()
            ?.let { row ->
                createPlayerFromRow(
                    row,
                    loadReceivedTrophies(row[TrophyPlayerTable.id].value)
                )
            }
    }

    suspend fun loadPlayerByName(name: String): TrophyPlayer? = suspendTransaction {
        TrophyPlayerTable
            .selectAll()
            .where(TrophyPlayerTable.name eq name)
            .firstOrNull()
            ?.let { row ->
                createPlayerFromRow(
                    row,
                    loadReceivedTrophies(row[TrophyPlayerTable.id].value)
                )
            }
    }

    suspend fun loadOrGetPlayerByName(name: String): TrophyPlayer? =
        loadPlayerByName(name)

    suspend fun giveTrophy(player: TrophyPlayer, trophy: ReceivedTrophy): Boolean =
        suspendTransaction {
            val playerRow = TrophyPlayerTable
                .selectAll()
                .where(TrophyPlayerTable.uuid eq player.uuid)
                .firstOrNull()
                ?: return@suspendTransaction false

            val playerId = playerRow[TrophyPlayerTable.id].value

            val trophyRow = TrophiesTable
                .selectAll()
                .where(TrophiesTable.uuid eq trophy.trophy.uuid)
                .firstOrNull()
                ?: return@suspendTransaction false

            val trophyId = trophyRow[TrophiesTable.id].value

            PlayerTrophiesTable.insert {
                it[this.playerId] = playerId
                it[this.trophyId] = trophyId
                it[this.receivedAt] = trophy.receivedAt
            }

            true
        }

    suspend fun takeTrophy(player: TrophyPlayer, trophy: Trophy): Boolean =
        suspendTransaction {
            val playerRow = TrophyPlayerTable
                .selectAll()
                .where(TrophyPlayerTable.uuid eq player.uuid)
                .firstOrNull()
                ?: return@suspendTransaction false

            val playerId = playerRow[TrophyPlayerTable.id].value

            val trophyRow = TrophiesTable
                .selectAll()
                .where(TrophiesTable.uuid eq trophy.uuid)
                .firstOrNull()
                ?: return@suspendTransaction false

            val trophyId = trophyRow[TrophiesTable.id].value

            val deletedRows = PlayerTrophiesTable.deleteWhere {
                (PlayerTrophiesTable.playerId eq playerId) and
                        (PlayerTrophiesTable.trophyId eq trophyId)
            }

            deletedRows > 0
        }

    suspend fun loadOrGetOrCreatePlayerByUuidAndName(
        uuid: UUID,
        name: String
    ): TrophyPlayer = suspendTransaction {
        loadPlayerByUuid(uuid)
            ?: run {
                TrophyPlayerTable.insert {
                    it[TrophyPlayerTable.uuid] = uuid
                    it[TrophyPlayerTable.name] = name
                }

                TrophyPlayer(uuid, name, ObjectArrayList())
            }
    }

    suspend fun savePlayer(trophyPlayer: TrophyPlayer) = suspendTransaction {
        val playerRow = TrophyPlayerTable
            .selectAll()
            .where(TrophyPlayerTable.uuid eq trophyPlayer.uuid)
            .firstOrNull()
            ?: return@suspendTransaction

        val playerId = playerRow[TrophyPlayerTable.id].value

        PlayerTrophiesTable.deleteWhere { PlayerTrophiesTable.playerId eq playerId }

        trophyPlayer.trophies.forEach { received ->
            val trophyId =
                TrophiesTable.selectAll().where(TrophiesTable.uuid eq received.trophy.uuid)
                    .firstOrNull()?.get(TrophiesTable.id)?.value ?: return@forEach

            PlayerTrophiesTable.insert {
                it[this.playerId] = playerId
                it[this.trophyId] = trophyId
                it[receivedAt] = received.receivedAt
            }
        }
    }

    private suspend fun loadReceivedTrophies(playerId: Long): ObjectList<ReceivedTrophy> =
        suspendTransaction {
            (PlayerTrophiesTable innerJoin TrophiesTable)
                .selectAll()
                .where(PlayerTrophiesTable.playerId eq playerId)
                .map { row ->
                    val trophy = trophyRepository.createTrophyFromRow(row)
                    createReceivedTrophyFromRow(row, trophy)
                }.toList().toMutableObjectList()
        }

    private fun createPlayerFromRow(
        row: ResultRow,
        trophies: ObjectList<ReceivedTrophy>
    ): TrophyPlayer =
        TrophyPlayer(
            row[TrophyPlayerTable.uuid],
            row[TrophyPlayerTable.name],
            trophies
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
