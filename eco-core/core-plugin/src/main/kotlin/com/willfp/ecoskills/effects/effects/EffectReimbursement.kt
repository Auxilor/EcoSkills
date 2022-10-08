package com.willfp.ecoskills.effects.effects

import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.enchantment.EnchantItemEvent

class EffectReimbursement : Effect(
    "reimbursement"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%chance%", NumberUtils.format(config.getDouble("chance-per-level") * level))
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handleLevelling(event: EnchantItemEvent) {
        if (this.config.getStrings("disabled-in-worlds").contains(event.enchanter.world.name)) {
            return
        }

        val player = event.enchanter
        if (!this.checkConditions(player)) {
            return
        }
        val cost = event.whichButton()+1

        val chance = config.getDouble("chance-per-level") * player.getEffectLevel(this)

        if (NumberUtils.randFloat(0.0, 100.0) < chance) {
//            event.isCancelled = true
            this.plugin.scheduler.runLater({
                player.giveExpLevels(cost)
            }, 2)
        }

    }
}