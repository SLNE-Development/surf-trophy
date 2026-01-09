plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.trophy.paper.PaperMain")
    generateLibraryLoader(false)
    foliaSupported(true)

    withCorePaper()

    authors.add("red")
}

dependencies {
    api(project(":surf-trophy-core"))
    runtimeOnly(project(":surf-trophy-backend"))
}
