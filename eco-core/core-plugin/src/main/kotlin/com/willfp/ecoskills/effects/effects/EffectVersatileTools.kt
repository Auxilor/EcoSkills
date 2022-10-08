package com.willfp.ecoskills.effects.effects

import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent

class EffectVersatileTools: Effect(
    "versatile_tools"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%percent_more%", NumberUtils.format(config.getDouble("percent-more-per-level") * level))
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
        if (this.config.getStrings("disabled-in-worlds").contains(event.entity.world.name)) {
            return
        }

        val player = event.damager

        if (player !is Player) {
            return
        }

        if (!player.inventory.itemInMainHand.type.toString().lowercase().contains("pickaxe")) {
            return
        }

        if (!this.checkConditions(player)) {
            return
        }

        val level = player.getEffectLevel(this)

        var multiplier = config.getDouble("percent-more-per-level") * level

        multiplier /= 100
        multiplier += 1

        event.damage = event.damage * multiplier
    }
}