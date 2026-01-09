package dev.slne.surf.trophy.backend.table

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object PlayerTrophiesTable : LongIdTable("trophy_player_trophies") {
    val playerId = long("player_id").references(TrophyPlayerTable.id)
    val trophyId = long("trophy_id").references(TrophiesTable.id)
    val receivedAt = long("trophy_receivedAt")
}