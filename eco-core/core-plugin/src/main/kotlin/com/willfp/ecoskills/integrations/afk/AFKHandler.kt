package com.willfp.ecoskills.integrations.afk

import org.bukkit.entity.Player

private val registry = mutableSetOf<AFKIntegration>()

fun registerIntegration(integration: AFKIntegration) {
    registry.add(integration)
}

val Player.isAfk: Boolean
    get() {
        for (afkIntegration in registry) {
            if (afkIntegration.isAfk(this)) {
                return true
            }
        }
        return false
    }