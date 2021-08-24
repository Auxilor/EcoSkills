package com.willfp.ecoskills.skills.skills

import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.skills.Skill
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.enchantment.EnchantItemEvent

class SkillEnchanting : Skill(
    "enchanting"
) {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handleLevelling(event: EnchantItemEvent) {
        val player = event.enchanter
        val cost = event.expLevelCost

        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        val xp = cost * this.config.getDouble("xp-per-level")
        player.giveSkillExperience(this, xp)
    }
}