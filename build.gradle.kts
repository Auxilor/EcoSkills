import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    `java-library`
    `maven-publish`
    kotlin("jvm") version "2.1.0"
    id("io.github.goooler.shadow") version "8.1.7"
    id("com.willfp.libreforge-gradle-plugin") version "1.0.0"
}

group = "com.willfp"
version = findProperty("version")!!
val libreforgeVersion = findProperty("libreforge-version")

base {
    archivesName.set(project.name)
}

dependencies {
    implementation(project(":eco-core:core-plugin"))
    implementation(project(":eco-core:core-nms:v1_21_4"))
    implementation(project(":eco-core:core-nms:v1_21_5"))
    implementation(project(":eco-core:core-nms:v1_21_6"))
    implementation(project(":eco-core:core-nms:v1_21_7"))
    implementation(project(":eco-core:core-nms:v1_21_8"))
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")
    apply(plugin = "io.github.goooler.shadow")

    repositories {
        mavenLocal()
        mavenCentral()

        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.auxilor.io/repository/maven-public/")
        maven("https://repo.codemc.org/repository/nms/")
        maven("https://jitpack.io")
    }

    dependencies {
        compileOnly("com.willfp:eco:6.77.0")
        compileOnly("org.jetbrains:annotations:23.0.0")
        compileOnly("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
    }

    java {
        withSourcesJar()
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    tasks {
        shadowJar {
            relocate("com.willfp.libreforge.loader", "com.willfp.ecoskills.libreforge.loader")
            relocate("com.willfp.ecomponent", "com.willfp.ecoskills.ecomponent")
        }

        compileKotlin {
            compilerOptions {
                jvmTarget = JvmTarget.JVM_21
            }
        }

        compileJava {
            options.isDeprecation = true
            options.encoding = "UTF-8"

            dependsOn(clean)
        }

        processResources {
            filesMatching(listOf("**plugin.yml", "**eco.yml")) {
                expand(
                    "version" to project.version,
                    "libreforgeVersion" to libreforgeVersion,
                    "pluginName" to rootProject.name
                )
            }
        }

        build {
            dependsOn(shadowJar)
        }
    }
}
