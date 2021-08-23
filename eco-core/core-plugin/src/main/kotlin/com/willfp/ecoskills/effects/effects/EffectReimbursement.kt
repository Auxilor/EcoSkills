package com.willfp.ecoskills.effects.effects

import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import com.willfp.ecoskills.giveSkillExperience
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.entity.EntityDamageEvent

class EffectReimbursement : Effect(
    "reimbursement"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%chance%", NumberUtils.format(config.getDouble("chance-per-level") * level))
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handleLevelling(event: EnchantItemEvent) {
        val player = event.enchanter
        val cost = event.expLevelCost

        val chance = config.getDouble("chance-per-level") * player.getEffectLevel(this)

        if (NumberUtils.randFloat(0.0, 100.0) < chance) {
            event.isCancelled = true
        }

        this.plugin.scheduler.runLater({
            player.giveExpLevels(cost)
        }, 2)
    }
}