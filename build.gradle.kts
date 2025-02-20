plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
}

group = "top.ninnana"
version = "1.0-SNAPSHOT"
val ktor_version: String by project

repositories {
    mavenCentral()
}

sourceSets {
    create("examples") {
        kotlin.srcDir("src/examples/kotlin")
        resources.srcDir("src/examples/resources")
        compileClasspath += main.get().output
        runtimeClasspath += main.get().output
    }
}

dependencies {
    implementation(kotlin("reflect"))

    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-cio-jvm:3.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    testImplementation(kotlin("test"))

    "examplesImplementation"("io.ktor:ktor-client-core:$ktor_version")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
    compilerOptions {
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }
}