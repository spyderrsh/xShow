import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    val kotlinVersion: String by System.getProperties()
    kotlin("plugin.serialization") version kotlinVersion
    kotlin("multiplatform") version kotlinVersion
    val kvisionVersion: String by System.getProperties()
//    id("io.kvision") version kvisionVersion
    id("org.jetbrains.compose")
}

version = "1.0.0-SNAPSHOT"
group = "com.spyderrsh"

repositories {
    mavenCentral()
    mavenLocal()
}

// Versions
val kotlinVersion: String by System.getProperties()
//val kvisionVersion: String by System.getProperties()
val koinVersion: String by System.getProperties()
val ktorVersion: String by project
val logbackVersion: String by project
val exposedVersion: String by project

val mainClassName = "io.ktor.server.netty.EngineMain"
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
    jvm {
        withJava()
        compilations.all {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xjsr305=strict")
            }
        }
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        mainRun {
            mainClass.set(mainClassName)
        }
    }
//    js(IR) {
//        browser {
//            commonWebpackConfig(Action {
//                outputFileName = "main.bundle.js"
//            })
//            runTask(Action {
//                sourceMaps = false
//                devServer = KotlinWebpackConfig.DevServer(
//                    open = false,
//                    port = 3000,
//                    proxy = mutableMapOf(
//                        "/kv/*" to "http://localhost:8080",
//                        "/xstatic/*" to mapOf(
//                            "target" to "http://localhost:8080",
//                            "router" to "http://localhost:3000",
//                        ),
//                        "/kvsse/*" to "http://localhost:8080",
//                        "/kvws/*" to mapOf("target" to "ws://localhost:8080", "ws" to true)
//                    ),
//                    static = mutableListOf("${layout.buildDirectory.asFile.get()}/processedResources/js/main")
//                )
//            })
//            testTask(Action {
//                useKarma {
//                    useChromeHeadless()
//                }
//            })
//        }
//        binaries.executable()
//    }
    wasmJs {
        moduleName = "xshow"
        browser {
            commonWebpackConfig {
                devServer = (devServer ?: KotlinWebpackConfig.DevServer(
                    port = 3001,
                    proxy = mutableMapOf(
                        "/xstatic/*" to mapOf(
                            "target" to "http://localhost:8080",
                            "router" to "http://localhost:3001",
                        ),
                    ),
                )).apply {
                    // Uncomment and configure this if you want to open a browser different from the system default
                    // open = mapOf(
                    //     "app" to mapOf(
                    //         "name" to "google chrome"
                    //     )
                    // )

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
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jsWasmMain by creating {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.material3)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("reflect"))
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-server-auth:$ktorVersion")
                implementation("io.ktor:ktor-server-compression:$ktorVersion")
                implementation("io.ktor:ktor-server-partial-content:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("ch.qos.logback:logback-classic:$logbackVersion")
                implementation("net.bramp.ffmpeg:ffmpeg:0.8.0")

                implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-crypt:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-json:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
                implementation("org.xerial:sqlite-jdbc:3.30.1")

                api( "io.insert-koin:koin-logger-slf4j:$koinVersion")

            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
//        val jsMain by getting {
//            dependencies {
//                implementation("io.kvision:kvision:$kvisionVersion")
//                implementation("io.kvision:kvision-bootstrap:$kvisionVersion")
//                implementation("io.kvision:kvision-toastify:$kvisionVersion")
//                implementation("io.kvision:kvision-jquery:$kvisionVersion")
//                implementation("io.kvision:kvision-state:$kvisionVersion")
//                implementation("io.kvision:kvision-state-flow:$kvisionVersion")
//                implementation("io.kvision:kvision-redux-kotlin:$kvisionVersion")
//                implementation("io.insert-koin:koin-core:$koinVersion")
//            }
//        }

        val wasmJsMain by getting {
            dependsOn(jsWasmMain)
        }
//        val jsTest by getting {
//            dependencies {
//                implementation(kotlin("test-js"))
//                implementation("io.kvision:kvision-testutils:$kvisionVersion")
//            }
//        }
    }
}

compose.experimental {
    web.application {}
}
