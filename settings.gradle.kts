pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        maven("https://repo.jpenilla.xyz/snapshots/")
        maven("https://jitpack.io")
    }
}

rootProject.name = "EcoSkills"

// Core
include(":eco-core")
include(":eco-core:core-plugin")
