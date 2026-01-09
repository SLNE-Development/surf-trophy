package dev.slne.surf.trophy.backend.table

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object TrophyPlayerTable : LongIdTable("trophy_players") {
    val uuid = uuid("player_uuid")
    val name = varchar("player_name", 16)
}