group = "com.willfp"
version = rootProject.version

val spigotVersion = "1.18.2-R0.1-SNAPSHOT"

dependencies {
    compileOnly("org.spigotmc:spigot:$spigotVersion")
}

configurations.compileOnly {
    resolutionStrategy {
        force("org.spigotmc:spigot:$spigotVersion")
    }
}