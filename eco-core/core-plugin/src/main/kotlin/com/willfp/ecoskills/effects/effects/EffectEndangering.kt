package com.willfp.ecoskills.effects.effects

import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent


class EffectEndangering : Effect(
    "endangering"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%chance%", NumberUtils.format(config.getDouble("chance-per-level") * level))
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
        var player = event.damager
        val victim = event.entity

        if (victim !is LivingEntity) {
            return
        }

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

        this.plugin.run {
            victim.noDamageTicks = 0
        }
    }
}