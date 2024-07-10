import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.multiplatform)
}

version = "1.0.0-SNAPSHOT"
group = "com.spyderrsh"


kotlin {
    jvmToolchain(17)
    js(IR) {

    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "xshow-app-shared"
        browser {
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared"))
                api(libs.kotlinx.coroutines)
                api(libs.kotlinx.serialization)
                api(libs.ktor.wasm.client.core)
                api(libs.ktor.wasm.client.serialization)
                api(libs.ktor.wasm.client.content.negotiation)
            }
        }

        val jsWasmMain by creating {
            dependsOn(commonMain)
        }
        val jsCommonMain by creating {
            dependsOn(commonMain)
        }

        jsMain.get().dependsOn(jsCommonMain)

        wasmJsMain.get().dependsOn(jsWasmMain)
    }
}
