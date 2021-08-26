package com.willfp.ecoskills.data

import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.expMultiplierCache
import org.bukkit.Bukkit

class SaveHandler {
    companion object {
        fun save(plugin: EcoSkillsPlugin) {
            if (Bukkit.getOnlinePlayers().isEmpty()) {
                return
            }
            if (plugin.configYml.getBool("log-autosaves")) {
                plugin.logger.info("Auto-Saving player data!")
            }
            plugin.dataYml.save()
            expMultiplierCache.clear()
            if (plugin.configYml.getBool("log-autosaves")) {
                plugin.logger.info("Saved data!")
            }
        }
    }

    class Runnable(
        private val plugin: EcoSkillsPlugin
    ) : java.lang.Runnable {
        override fun run() {
            save(plugin)
        }
    }
}