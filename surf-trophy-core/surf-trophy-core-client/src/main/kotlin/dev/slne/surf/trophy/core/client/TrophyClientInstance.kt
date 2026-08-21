package dev.slne.surf.trophy.core.client

import dev.slne.surf.rabbitmq.api.ClientRabbitMQApi
import dev.slne.surf.trophy.core.common.TrophyInstance
import java.nio.file.Path

/**
 * The platform-neutral client side of the trophy plugin.
 */
abstract class TrophyClientInstance : TrophyInstance {

    /** The directory the RabbitMQ client reads its configuration from. */
    abstract val dataPath: Path

    override val rabbitApi: ClientRabbitMQApi = ClientRabbitMQApi.create(CONNECTION_NAME, dataPath)

    suspend fun onLoad() {
        rabbitApi.freezeAndConnect()
    }

    suspend fun onEnable() {
    }

    suspend fun onDisable() {
        rabbitApi.disconnect()
    }

    companion object {
        private const val CONNECTION_NAME = "surf-trophy"

        val INSTANCE get() = TrophyInstance.INSTANCE as TrophyClientInstance
    }
}

val rabbitApi get() = TrophyClientInstance.INSTANCE.rabbitApi
