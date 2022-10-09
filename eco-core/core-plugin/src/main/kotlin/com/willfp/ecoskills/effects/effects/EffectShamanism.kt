package com.willfp.ecoskills.effects.effects

import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityRegainHealthEvent

class EffectShamanism: Effect(
    "shamanism"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%percent_faster%", NumberUtils.format(config.getDouble("percent-faster-per-level") * level))
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: EntityRegainHealthEvent) {
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

        val level = player.getEffectLevel(this)

        var multiplier = config.getDouble("percent-faster-per-level") * level

        multiplier /= 100
        multiplier += 1

        event.amount = event.amount * multiplier
    }
}