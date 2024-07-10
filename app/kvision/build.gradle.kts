import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.multiplatform)
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
    @OptIn(ExperimentalWasmDsl::class)
    js(IR) {
        browser {
            runTask(Action {
                mainOutputFileName = "main.bundle.js"
                sourceMaps = false
                devServer = KotlinWebpackConfig.DevServer(
                    open = false,
                    port = 3000,
                    proxy = mutableListOf(
                        KotlinWebpackConfig.DevServer.Proxy(
                            mutableListOf("/xstatic"),
                            "http://localhost:8080"
                        )
                    ),
                    static = mutableListOf(
                        project.projectDir.path,
                        project.projectDir.path + "/src/jsMain/web/"

                    )
                )
            })
            webpackTask(Action {
                mainOutputFileName = "main.bundle.js"
            })
            testTask(Action {
                useKarma {
                    useChromeHeadless()
                }
            })
        }
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation(project(":app:shared"))
            }
        }
        val mainJs by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.kvision.core)
                implementation(libs.kvision.bootstrap)
                implementation(libs.kvision.toastify)
                implementation(libs.kvision.jquery)
                implementation(libs.kvision.state)
                implementation(libs.kvision.state.flow)
                implementation(libs.kvision.redux.kotlin)
                implementation(libs.koin.core)
            }
        }
        jsMain.get().dependsOn(mainJs)
    }
}
