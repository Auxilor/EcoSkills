package com.willfp.ecoskills.skills.skills

import com.willfp.eco.core.integrations.afk.AFKManager
import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.skills.Skill
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.enchantment.EnchantItemEvent

class SkillEnchanting : Skill(
    "enchanting"
) {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handleLevelling(event: EnchantItemEvent) {
        if (this.config.getStrings("disabled-in-worlds").contains(event.enchanter.world.name)) {
            return
        }
        val player = event.enchanter
        val cost = event.expLevelCost

        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        if (plugin.configYml.getBool("skills.prevent-levelling-while-afk") && AFKManager.isAfk(player)) {
            return
        }

        val xp = cost * this.config.getDouble("xp-per-level")
        player.giveSkillExperience(this, xp)
    }
}