package com.willfp.ecoskills.integrations.enchantgui

import com.gmail.legamemc.enchantgui.api.event.PlayerEnchantItemEvent
import com.willfp.eco.core.integrations.afk.AFKManager
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.skills.Skills
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class EnchantGuiHandler: Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handleLevelling(event: PlayerEnchantItemEvent) {
        val skill = Skills.ENCHANTING
        if (skill.config.getStrings("disabled-in-worlds").contains(event.player.world.name)) {
            return
        }
        val player = event.player
        val cost = event.level

        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        if (EcoSkillsPlugin.getInstance()
                .configYml.getBool("skills.prevent-levelling-while-afk") && AFKManager.isAfk(player)) {
            return
        }

        val xp = cost * skill.config.getDouble("xp-per-level")
        player.giveSkillExperience(skill, xp)
    }
}