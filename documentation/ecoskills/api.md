---
title: "API"
sidebar_position: 9
---

This page is for developers who want to hook into EcoSkills from their own plugin: read player skill, stat, and effect levels, or grant XP programmatically. EcoSkills is open-source, so you can also read the code directly.

## Source code

The full source is on GitHub: [Auxilor/EcoSkills](https://github.com/Auxilor/EcoSkills).

## Adding the dependency

1. Add the Auxilor repository to your `build.gradle.kts`.
2. Add EcoSkills as a `compileOnly` dependency, swapping `<version>` for the version you want.

```kotlin
repositories {
    maven("https://repo.auxilor.io/repository/maven-public/") // Auxilor repository
}

dependencies {
    compileOnly("com.willfp:EcoSkills:<version>") // EcoSkills API, provided at runtime by the plugin
}
```

The latest version available on the repo can be found [here](https://github.com/Auxilor/EcoSkills/tags).

<hr/>

## Where to go next

- **eco framework:** the shared APIs (effects, conditions, triggers) live in the [eco framework](https://github.com/Auxilor/eco).
- **Configuring skills:** to drive behaviour from config instead of code, see [How to make a Skill](how-to-make-a-skill).