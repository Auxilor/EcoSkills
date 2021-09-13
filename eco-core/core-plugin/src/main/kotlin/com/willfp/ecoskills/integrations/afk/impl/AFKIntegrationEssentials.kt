package com.willfp.ecoskills.integrations.afk.impl

import com.earth2me.essentials.Essentials
import com.willfp.ecoskills.integrations.afk.AFKIntegration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class AFKIntegrationEssentials: AFKIntegration {
    private val ess: Essentials = JavaPlugin.getPlugin(Essentials::class.java)

    override fun isAfk(player: Player): Boolean {
        return ess.getUser(player) != null && ess.getUser(player).isAfk
    }

    override fun getPluginName(): String {
        return "Essentials"
    }
}