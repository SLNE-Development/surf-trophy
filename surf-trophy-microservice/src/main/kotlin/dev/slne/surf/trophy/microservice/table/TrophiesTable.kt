package dev.slne.surf.trophy.microservice.table

import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object TrophiesTable : LongIdTable("trophy_trophies") {
    val uuid = nativeUuid("trophy_uuid")
    val name = text("trophy_name")
    val description = text("trophy_description")
    val item = largeText("trophy_item_bytes")
}