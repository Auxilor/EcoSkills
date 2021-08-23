package com.willfp.ecoskills.data

import com.willfp.eco.core.EcoPlugin
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.PlayerHelper
import org.bukkit.Bukkit

class DataRunnable (
    private val plugin: EcoSkillsPlugin
) : Runnable {
    override fun run() {
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            return
        }
        if (plugin.configYml.getBool("log-autosaves")) {
            plugin.logger.info("Auto-Saving player data!")
        }
        plugin.dataYml.save()
        PlayerHelper.expMultiplierCache.clear()
        if (plugin.configYml.getBool("log-autosaves")) {
            plugin.logger.info("Saved data!")
        }
    }
}