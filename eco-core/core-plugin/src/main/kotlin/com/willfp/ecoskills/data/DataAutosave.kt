package com.willfp.ecoskills.data

import com.willfp.eco.core.EcoPlugin
import com.willfp.ecoskills.EcoSkillsPlugin
import org.bukkit.Bukkit

class DataAutosave (
    private val plugin: EcoSkillsPlugin
) : Runnable {
    override fun run() {
        Bukkit.getLogger().info("Auto-Saving player data!")
        plugin.dataYml.save()
        Bukkit.getLogger().info("Saved data!")
    }
}