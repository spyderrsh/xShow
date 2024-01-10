pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        mavenLocal()
    }

    plugins {
        val kotlinGeneration = extra["kotlin.generation"] as String
        val kotlinVersion = extra["kotlin.version.$kotlinGeneration"] as String
        val agpVersion = extra["agp.version"] as String
        val composeVersion = extra["compose.wasm.version.$kotlinGeneration"] as String

        kotlin("jvm").version(kotlinVersion)
        kotlin("multiplatform").version(kotlinVersion)
        kotlin("plugin.serialization").version(kotlinVersion)
        kotlin("android").version(kotlinVersion)
        id("com.android.base").version(agpVersion)
        id("com.android.application").version(agpVersion)
        id("com.android.library").version(agpVersion)
        id("org.jetbrains.compose").version(composeVersion)
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "xshow"
include("server")
include("shared")
