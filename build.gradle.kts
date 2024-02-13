plugins {
    val kotlinVersion: String by System.getProperties()
    val kvisionVersion: String by System.getProperties()
    val shadowVersion: String by System.getProperties()
    val micronautPluginsVersion: String by System.getProperties()

    kotlin("plugin.serialization") version kotlinVersion apply false
    kotlin("multiplatform") version kotlinVersion apply false
    kotlin("plugin.allopen") version kotlinVersion apply false
    kotlin("kapt") version kotlinVersion apply false
    id("com.github.johnrengelman.shadow") version shadowVersion
    id("io.kvision") version kvisionVersion apply false
    id("io.micronaut.application") version micronautPluginsVersion apply false
    id("io.micronaut.aot") version micronautPluginsVersion apply false
}

version = "0.0.1-SNAPSHOT"
group = "com.example"

subprojects {
    group = "com.example"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

