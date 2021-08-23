package com.willfp.ecoskills.effects.effects

import com.willfp.eco.core.scheduling.RunnableTask
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.util.concurrent.atomic.AtomicInteger


class EffectStrongImpact : Effect(
    "strong_impact"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%chance%", NumberUtils.format(config.getDouble("chance-per-level") * level))
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
        var player = event.damager

        if (player is Projectile) {
            if (player.shooter !is Player) {
                return
            } else {
                player = player.shooter as Player
            }
        }

        if (player !is Player) {
            return
        }

        val level = player.getEffectLevel(this)

        if (NumberUtils.randFloat(0.0, 100.0) >= config.getDouble("chance-per-level") * level) {
            return
        }

        event.damage = event.damage * config.getDouble("multiplier")
    }
}