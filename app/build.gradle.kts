import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.compose)
}

version = "1.0.0-SNAPSHOT"
group = "com.spyderrsh"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev/")
    maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
}

// Versions
val koinVersion: String by System.getProperties()
val ktorVersion: String by project
val logbackVersion: String by project
val exposedVersion: String by project

kotlin {
    jvmToolchain(17)
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "xshow"
        browser {
            commonWebpackConfig {
                outputFileName = "xshow.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    // Uncomment and configure this if you want to open a browser different from the system default
                    // open = mapOf(
                    //     "app" to mapOf(
                    //         "name" to "google chrome"
                    //     )
                    // )
                    proxy = (proxy ?: mutableListOf()).apply {

                        add(
                            KotlinWebpackConfig.DevServer.Proxy(
                                mutableListOf("/xstatic"),
                                "http://localhost:8080"
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
            dependsOn(commonMain)
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
                implementation(libs.ktor.wasm.client.core)
                implementation(libs.ktor.wasm.client.serialization)
                implementation(libs.ktor.wasm.client.content.negotiation)
//                implementation(libs.ktor.wasm.events)
                implementation(project(":composeWebInterop"))
                implementation(libs.coil.compose)
                implementation(libs.coil.ktor)
//                implementation("org.jetbrains.skiko:skiko-wasm-js:0.8.9")


            }
        }

        wasmJsMain.get().dependsOn(jsWasmMain)
    }
}
