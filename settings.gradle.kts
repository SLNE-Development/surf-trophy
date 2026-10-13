include("surf-trophy-api")
include("surf-trophy-paper")
include("surf-trophy-minestom")
include("surf-trophy-microservice")
include("surf-trophy-core:surf-trophy-core-common")
include("surf-trophy-core:surf-trophy-core-client")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://reposilite.slne.dev/releases")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.slne.surf.api.gradle.settings") version "+"
}
