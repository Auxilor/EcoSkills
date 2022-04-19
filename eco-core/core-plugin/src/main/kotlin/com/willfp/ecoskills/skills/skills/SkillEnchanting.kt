package com.willfp.ecoskills.skills.skills

import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.skills.Skill
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.enchantment.EnchantItemEvent

class SkillEnchanting : Skill(
    "enchanting"
) {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handleLevelling(event: EnchantItemEvent) {
        val player = event.enchanter.filterSkillEnabled() ?: return

        val cost = event.expLevelCost

        val xp = cost * this.config.getDouble("xp-per-level")
        player.giveSkillExperience(this, xp)
    }
}