plugins {
    id("io.papermc.paperweight.userdev")
}

group = "com.willfp"
version = rootProject.version

dependencies {
    implementation(project(":eco-core:core-nms:v1_21_4", configuration = "shadow"))
    paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")
}

tasks {
    build {
        dependsOn(reobfJar)
    }

    reobfJar {
        mustRunAfter(shadowJar)
    }

    shadowJar {
        relocate(
            "com.willfp.ecoskills.proxy.v1_21_4",
            "com.willfp.ecoskills.proxy.v1_21_8",
        )
    }
}
