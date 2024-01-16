plugins {
    val kotlinVersion: String by System.getProperties()
    kotlin("jvm")
    kotlin("plugin.serialization") version kotlinVersion
    application
}

group = "com.spyderrsh.xshow"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

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
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-compression:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-partial-content:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets:$ktorVersion")
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