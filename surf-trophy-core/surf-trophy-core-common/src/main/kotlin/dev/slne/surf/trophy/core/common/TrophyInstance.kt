package dev.slne.surf.trophy.core.common

import dev.slne.surf.rabbitmq.api.RabbitMQApi
import dev.slne.surf.surfapi.core.api.util.requiredService

private val instance = requiredService<TrophyInstance>()

interface TrophyInstance {
    val rabbitApi: RabbitMQApi

    companion object : TrophyInstance by instance {
        val INSTANCE get() = instance
    }
}