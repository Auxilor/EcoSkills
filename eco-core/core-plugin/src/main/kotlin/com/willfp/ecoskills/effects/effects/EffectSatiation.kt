package com.willfp.ecoskills.effects.effects

import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.FoodLevelChangeEvent

class EffectSatiation: Effect(
    "satiation"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%percent_less%", NumberUtils.format(config.getDouble("percent-less-hunger-per-level") * level))
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: FoodLevelChangeEvent) {
        if (this.config.getStrings("disabled-in-worlds").contains(event.entity.world.name)) {
            return
        }

        val player = event.entity

        if (player !is Player) {
            return
        }

        if (!this.checkConditions(player)) {
            return
        }

        if (event.foodLevel > player.foodLevel) {
            return
        }

        val level = player.getEffectLevel(this)

        if (NumberUtils.randFloat(0.0, 100.0) >= level * this.config.getDouble("percent-less-hunger-per-level")) {
            return
        }

        event.isCancelled = true

    }
}