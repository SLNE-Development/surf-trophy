package dev.slne.surf.trophy.microservice

import com.google.auto.service.AutoService
import dev.slne.surf.database.DatabaseApi
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.microservice.api.microservice.Microservice
import dev.slne.surf.rabbitmq.api.ServerRabbitMQApi
import dev.slne.surf.trophy.microservice.rabbit.handler.PlayerTrophiesHandler
import dev.slne.surf.trophy.microservice.rabbit.handler.TrophiesHandler
import dev.slne.surf.trophy.microservice.table.PlayerTrophiesTable
import dev.slne.surf.trophy.microservice.table.SelectedTrophyTable
import dev.slne.surf.trophy.microservice.table.TrophiesTable
import kotlin.io.path.Path

@AutoService(Microservice::class)
class TrophyMicroservice : Microservice() {
    override val dataPath = Path("config")
    private val databaseApi = DatabaseApi.create(dataPath)
    private val rabbitApi = ServerRabbitMQApi.create("surf-trophy", dataPath)

    override suspend fun onBootstrap(args: List<String>) {
        suspendTransaction {
            SchemaUtils.create(
                PlayerTrophiesTable,
                SelectedTrophyTable,
                TrophiesTable
            )
        }

        rabbitApi.registerRequestHandler(TrophiesHandler)
        rabbitApi.registerRequestHandler(PlayerTrophiesHandler)
        rabbitApi.freezeAndConnect()
    }

    override suspend fun onDisable() {
        rabbitApi.disconnect()
        databaseApi.shutdown()
    }
}