val ktor_version: String by project
val project_version: String by project

plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "1.8.0"
}

allprojects {
    group = "org.sithra.synthetic"
    version = project_version
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.plugin.serialization")
    }
    dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
        api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    }
    kotlin {
        jvmToolchain(21)
        compilerOptions {
            optIn.add("kotlin.uuid.ExperimentalUuidApi")
        }
    }
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    subprojects.forEach { testImplementation(it) }
    testImplementation("io.ktor:ktor-client-cio-jvm:$ktor_version")
    testImplementation(kotlin("test"))
}