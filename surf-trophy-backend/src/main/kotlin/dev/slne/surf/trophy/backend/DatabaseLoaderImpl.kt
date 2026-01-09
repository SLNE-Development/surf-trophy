package dev.slne.surf.trophy.backend

import com.google.auto.service.AutoService
import dev.slne.surf.database.DatabaseApi
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.trophy.backend.table.PlayerTrophiesTable
import dev.slne.surf.trophy.backend.table.TrophiesTable
import dev.slne.surf.trophy.backend.table.TrophyPlayerTable
import dev.slne.surf.trophy.core.database.DatabaseLoader
import net.kyori.adventure.util.Services
import java.nio.file.Path

@AutoService(DatabaseLoader::class)
class DatabaseLoaderImpl : DatabaseLoader, Services.Fallback {
    lateinit var databaseApi: DatabaseApi
    override suspend fun connect(dataPath: Path) {
        databaseApi = DatabaseApi.create(dataPath)

        suspendTransaction {
            SchemaUtils.create(TrophyPlayerTable, TrophiesTable, PlayerTrophiesTable)
        }
    }

    override fun disconnect() {
        databaseApi.shutdown()
    }
}