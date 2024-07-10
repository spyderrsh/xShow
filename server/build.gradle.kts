plugins {
    val kotlinVersion: String by System.getProperties()
    kotlin("jvm")
    kotlin("plugin.serialization") version kotlinVersion
    application
}

group = "com.spyderrsh.xshow"
version = "1.0.0-SNAPSHOT"

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
// Versions
val kotlinVersion: String by System.getProperties()
//val kvisionVersion: String by System.getProperties()
val koinVersion: String by System.getProperties()
val ktorVersion: String by project
val logbackVersion: String by project
val exposedVersion: String by project

val mainClassName = "io.ktor.server.netty.EngineMain"


dependencies {
    implementation(kotlin("reflect"))
    implementation(project(":shared"))
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.compression)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.partial.content)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.serialization)
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("net.bramp.ffmpeg:ffmpeg:0.8.0")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-crypt:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-json:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
    implementation("org.xerial:sqlite-jdbc:3.41.2.2")

    api("io.insert-koin:koin-logger-slf4j:$koinVersion")
    api("io.insert-koin:koin-ktor:$koinVersion")

}

kotlin {
    jvmToolchain(17)

    compilerOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

application {
    mainClass.set(mainClassName)
}