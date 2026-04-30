import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("io.papermc.paperweight.userdev")
}

group = "com.willfp"
version = rootProject.version

dependencies {
    implementation(project(":eco-core:core-nms:v1_21_8", configuration = "shadow"))
    paperweight.paperDevBundle("26.1.1.build.+")
}

tasks {
    shadowJar {
        relocate(
            "com.willfp.ecoskills.proxy.v1_21_8",
            "com.willfp.ecoskills.proxy.v26_1_1",
        )

        duplicatesStrategy = DuplicatesStrategy.FAIL
    }

    compileJava {
        options.release.set(25)
    }

    compileKotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_25)
        }
    }
}
