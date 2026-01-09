package dev.slne.surf.trophy.backend.table

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import dev.slne.surf.trophy.core.util.itemStackFromString
import dev.slne.surf.trophy.core.util.itemStackToString

object TrophiesTable : LongIdTable("trophy_trophies") {
    val uuid = uuid("trophy_uuid")
    val name = text("trophy_name")
    val description = text("trophy_description")
    val item = largeText("trophy_item_bytes").transform(
        { itemStackFromString(it) },
        { itemStackToString(it) })
}