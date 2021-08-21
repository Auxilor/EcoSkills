package com.willfp.ecoskills.effects.effects

import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent

class EffectVersatileTools: Effect(
    "versatile_tools"
) {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
        val player = event.damager

        if (player !is Player) {
            return
        }

        if (!player.inventory.itemInMainHand.type.toString().lowercase().contains("pickaxe")) {
            return
        }

        val level = player.getEffectLevel(this)

        var multiplier = config.getDouble("percent-more-per-level") * level

        multiplier /= 100
        multiplier += 1

        event.damage = event.damage * multiplier
    }
}