import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    val kotlinVersion: String by System.getProperties()
    kotlin("plugin.serialization") version kotlinVersion
    kotlin("multiplatform") version kotlinVersion
    id("org.jetbrains.compose")
}

version = "1.0.0-SNAPSHOT"
group = "com.spyderrsh"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
}

// Versions
val kotlinVersion: String by System.getProperties()
val koinVersion: String by System.getProperties()
val ktorVersion: String by project
val logbackVersion: String by project
val exposedVersion: String by project


afterEvaluate {
    extensions.findByType(ComposeExtension::class.java)?.apply {
        val kotlinGeneration = project.property("kotlin.generation")
        val composeCompilerVersion = project.property("compose.compiler.version.$kotlinGeneration") as String
        kotlinCompilerPlugin.set(composeCompilerVersion)
        val kotlinVersion = project.property("kotlin.version.$kotlinGeneration") as String
        kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=$kotlinVersion")
    }
}
kotlin {
    jvmToolchain(17)
    wasmJs {
        moduleName = "xshow"
        browser {
            commonWebpackConfig {
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    // Uncomment and configure this if you want to open a browser different from the system default
                    // open = mapOf(
                    //     "app" to mapOf(
                    //         "name" to "google chrome"
                    //     )
                    // )
                    proxy = (proxy ?: mutableMapOf()).apply {
                        put(
                            "/xstatic/*", mapOf(
                                "target" to "http://localhost:8080",
                                "router" to "http://localhost:8081",
                            )
                        )
                    }

                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(project.rootDir.path)
                        add(project.rootDir.path + "/src/jsMain/web/")
                        add(project.rootDir.path + "/nonAndroidMain/")
                        add(project.rootDir.path + "/webApp/")
                    }
                }
            }
        }
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared"))
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
            }
        }
        val jsWasmMain by creating {
            dependencies {
                implementation(libs.kotlinx.coroutines)
                implementation(libs.kotlinx.serialization)
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.material3)
                implementation("io.ktor:ktor-client-core:3.0.0-wasm1")
                implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0-wasm1")
                implementation("io.ktor:ktor-client-content-negotiation:3.0.0-wasm1")
                implementation(project(":composeWebInterop"))
                implementation("io.coil-kt.coil3:coil-compose:3.0.0-alpha06")

            }
        }

        val wasmJsMain by getting {
            dependsOn(jsWasmMain)
        }
    }
}

compose.experimental {
    web.application {}
}
