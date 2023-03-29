group = "com.willfp"
version = rootProject.version

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
    compileOnly("com.github.LegameMc:EnchantGui-API:1.0")
    compileOnly("commons-lang:commons-lang:2.6")
    compileOnly("com.github.ben-manes.caffeine:caffeine:3.0.6")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    compileOnly("com.willfp:EcoEnchants:9.0.0")
}

publishing {
    publications {
        register("maven", MavenPublication::class) {
            from(components["java"])
            artifactId = rootProject.name
        }
    }

    publishing {
        repositories {
            maven {
                name = "auxilor"
                url = uri("https://repo.auxilor.io/repository/maven-releases/")
                credentials {
                    username = System.getenv("MAVEN_USERNAME")
                    password = System.getenv("MAVEN_PASSWORD")
                }
            }
        }
    }
}

tasks {
    build {
        dependsOn(publishToMavenLocal)
    }
}
