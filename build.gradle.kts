import java.util.*

val ktor_version: String by project
val project_version: String by project
val secrets: Properties by lazy {
    project.rootProject.file("secrets.properties").reader().use {
        Properties().apply { load(it) }
    }
}

plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "1.8.0"
    id("maven-publish")
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
        plugin("maven-publish")
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
    configure<PublishingExtension> {
        publishing {
            repositories {
                maven {
                    name = "GitHubPackages"
                    url = uri("https://maven.pkg.github.com/SithraBot/synthetic")
                    credentials {
                        username = System.getenv("GITHUB_USERNAME") ?: secrets.getProperty("github.username")!!
                        password = System.getenv("GITHUB_TOKEN") ?: secrets.getProperty("github.token")!!
                    }
                }
            }
            publications {
                create<MavenPublication>("gpr") {
                    from(components["java"])
                }
            }
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