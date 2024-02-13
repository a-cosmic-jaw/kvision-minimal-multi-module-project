pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
}

rootProject.name = "kvision-minimal-multi-module-project"

include(":addressbook-fullstack-micronaut")

project(":addressbook-fullstack-micronaut").name = "addressbook-fullstack-micronaut"
