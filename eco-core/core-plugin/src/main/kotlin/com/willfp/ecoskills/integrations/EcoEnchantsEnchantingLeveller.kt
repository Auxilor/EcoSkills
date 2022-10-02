package com.willfp.ecoskills.integrations

import com.willfp.eco.core.fast.fast
import com.willfp.ecoenchants.enchants.wrap
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.skills.Skills
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent

class EcoEnchantsEnchantingLeveller(
    private val plugin: EcoSkillsPlugin
) : Listener {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handleLevelling(event: EnchantItemEvent) {
        val player = event.enchanter

        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        this.plugin.scheduler.runLater({
            if (event.item.fast().enchants.keys.any { it.wrap().type.id == "special" }) {
                val toGive = Skills.ENCHANTING.config.getDouble("ecoenchants.xp-for-special")

                player.giveSkillExperience(Skills.ENCHANTING, toGive)
            }
        }, 2)
    }
}