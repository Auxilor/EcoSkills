group = "com.willfp"
version = rootProject.version

val spigotVersion = "1.21.3-R0.1-SNAPSHOT"

dependencies {
    compileOnly("org.spigotmc:spigot:$spigotVersion")
}

configurations.compileOnly {
    resolutionStrategy {
        force("org.spigotmc:spigot:$spigotVersion")
    }
}

tasks {
    compileJava {
        options.release = 21
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = "21"
        }
    }
}
