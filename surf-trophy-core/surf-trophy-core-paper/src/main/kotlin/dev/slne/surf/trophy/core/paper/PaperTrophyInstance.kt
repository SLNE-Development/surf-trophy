package dev.slne.surf.trophy.core.paper

import dev.slne.surf.rabbitmq.api.ClientRabbitMQApi
import dev.slne.surf.trophy.core.common.TrophyInstance

interface PaperTrophyInstance : TrophyInstance {
    val paperLoader: PaperLoader

    override val rabbitApi: ClientRabbitMQApi get() = paperLoader.rabbitApi

    companion object : PaperTrophyInstance by TrophyInstance.INSTANCE as PaperTrophyInstance {
        val INSTANCE get() = TrophyInstance.INSTANCE
    }
}