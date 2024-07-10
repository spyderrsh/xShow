pluginManagement {
    listOf(repositories, dependencyResolutionManagement.repositories).forEach {
        it.apply {
            google()
            gradlePluginPortal()
            mavenCentral()
            maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
            maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
            maven("https://androidx.dev/storage/compose-compiler/repository")
            maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
            maven("https://maven.pkg.jetbrains.space/public/p/compose/dev/")
            maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        }
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("com.google.cloud.tools.appengine")) {
                useModule("com.google.cloud.tools:appengine-gradle-plugin:${requested.version}")
            }
        }
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "xshow"
include("server")
include("shared")
include("composeWebInterop")
include("app")
include("app:kvision")
include("app:shared")