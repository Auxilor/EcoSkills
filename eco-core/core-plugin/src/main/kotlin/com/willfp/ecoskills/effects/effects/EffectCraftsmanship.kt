package com.willfp.ecoskills.effects.effects

import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerItemDamageEvent

class EffectCraftsmanship : Effect(
    "craftsmanship"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%percent_less%", NumberUtils.format(config.getDouble("percent-less-per-level") * level))
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: PlayerItemDamageEvent) {
        if (this.config.getStrings("disabled-in-worlds").contains(event.player.world.name)) {
            return
        }

        val player = event.player

        if (!this.checkConditions(player)) {
            return
        }

        if (!event.item.type.toString().lowercase().contains("axe")) {
            return
        }

        val level = player.getEffectLevel(this)

        if (NumberUtils.randFloat(0.0, 100.0) < config.getDouble("percent-less-per-level") * level) {
            event.isCancelled = true
        }
    }
}