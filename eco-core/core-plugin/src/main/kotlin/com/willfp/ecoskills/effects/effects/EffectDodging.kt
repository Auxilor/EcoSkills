package com.willfp.ecoskills.effects.effects

import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent

class EffectDodging: Effect(
    "dodging"
) {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: EntityDamageEvent) {
        val player = event.entity

        if (player !is Player) {
            return
        }

        if (event.cause == EntityDamageEvent.DamageCause.FALL) {
            return
        }

        val level = player.getEffectLevel(this)

        val chance = config.getDouble("chance-per-level") * level

        if (NumberUtils.randFloat(0.0, 100.0) < chance) {
            event.isCancelled = true
        }
    }
}