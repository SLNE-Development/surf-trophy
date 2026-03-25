include("surf-trophy-api")
include("surf-trophy-paper")
include("surf-trophy-microservice")
include("surf-trophy-core:surf-trophy-core-common")
include("surf-trophy-core:surf-trophy-core-paper")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.slne.dev/repository/maven-public/") { name = "maven-public" }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.slne.surf.surfapi.gradle.settings") version "1.21.11+"
}