package dev.slne.surf.trophy.backend.table

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.ReferenceOption
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object SelectedTrophyTable : LongIdTable("trophy_selected_trophies") {
    val playerId = long("player_id").references(TrophyPlayerTable.id, ReferenceOption.CASCADE)
    val trophyId = long("trophy_id").references(TrophiesTable.id, ReferenceOption.CASCADE)
}