import dev.slne.surf.api.gradle.util.slneReleases
import dev.slne.surf.microservice.gradle.plugin.rabbit.RabbitModule

plugins {
    id("dev.slne.surf.api.gradle.minestom")
    id("dev.slne.surf.microservice")
}

surfMinestomApi {
    withCoreMinestom()
}

surfMicroservice {
    withRabbitModule(RabbitModule.CLIENT_API)
}

repositories {
    mavenLocal()
}

minestomPluginFile {
    id = "surf-trophy-minestom"
    main = "dev.slne.surf.trophy.minestom.SurfTrophyMinestomPlugin"
    authors = listOf("red")

    pluginDependencies {
        register("surf-rabbitmq-minestom")
    }
}

dependencies {
    api(projects.surfTrophyCore.surfTrophyCoreClient)
}

publishing {
    repositories {
        slneReleases()
    }
}
