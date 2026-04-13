package dev.slne.surf.trophy.core.common

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.rabbitmq.api.RabbitMQApi

private val instance = requiredService<TrophyInstance>()

interface TrophyInstance {
    val rabbitApi: RabbitMQApi

    companion object : TrophyInstance by instance {
        val INSTANCE get() = instance
    }
}