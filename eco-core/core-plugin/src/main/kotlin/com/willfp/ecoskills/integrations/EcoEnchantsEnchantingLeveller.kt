package com.willfp.ecoskills.integrations

import com.willfp.ecoenchants.enchantments.EcoEnchants
import com.willfp.ecoenchants.enchantments.meta.EnchantmentType
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
    fun handleLeveling(event: EnchantItemEvent) {
        val player = event.enchanter

        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        this.plugin.scheduler.runLater({
            if (EcoEnchants.hasAnyOfType(event.item, EnchantmentType.SPECIAL)) {
                val toGive = Skills.ENCHANTING.config.getDouble("ecoenchants.xp-for-special")

                player.giveSkillExperience(Skills.ENCHANTING, toGive)
            }
        }, 2)
    }
}