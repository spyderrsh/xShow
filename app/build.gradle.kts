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


kotlin {
    jvmToolchain(17)
    js(IR) {

    }
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
                        add(project.projectDir.path)
                        add(project.projectDir.path + "/src/jsMain/web/")
                        add(project.projectDir.path + "/nonAndroidMain/")
                        add(project.projectDir.path + "/webApp/")
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
                implementation(project(":app:shared"))
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
            }
        }
        val jsWasmMain by creating {
            dependsOn(commonMain)
            dependencies {
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
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
