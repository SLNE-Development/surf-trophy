package dev.slne.surf.trophy.microservice.table

import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.ReferenceOption
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object PlayerTrophiesTable : LongIdTable("trophy_player_trophies") {
    val playerUuid = nativeUuid("player_uuid").index()
    val trophyId = long("trophy_id").references(TrophiesTable.id, ReferenceOption.CASCADE)
    val receivedAt = long("trophy_receivedAt")
}
