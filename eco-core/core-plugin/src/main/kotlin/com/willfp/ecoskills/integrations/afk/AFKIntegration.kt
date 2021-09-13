package com.willfp.ecoskills.integrations.afk

import com.willfp.eco.core.integrations.Integration
import org.bukkit.entity.Player

interface AFKIntegration : Integration {
    fun isAfk(player: Player): Boolean
}