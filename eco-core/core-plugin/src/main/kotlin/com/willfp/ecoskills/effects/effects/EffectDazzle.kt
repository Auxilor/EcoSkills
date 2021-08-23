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
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import java.util.concurrent.atomic.AtomicInteger


class EffectDazzle : Effect(
    "dazzle"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%chance%", NumberUtils.format(config.getDouble("chance-per-level") * level))
            .replace("%seconds%", NumberUtils.format(((config.getInt("ticks-per-level") * level) + config.getInt("base-ticks")) / 20.0))
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

        val victim = event.entity

        if (victim !is LivingEntity) {
            return
        }

        val level = player.getEffectLevel(this)

        if (NumberUtils.randFloat(0.0, 100.0) >= config.getDouble("chance-per-level") * level) {
            return
        }

        val duration = (config.getInt("ticks-per-level") * level) + config.getInt("base-ticks")

        victim.setVelocity(Vector(0, 0, 0))
        victim.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, duration, level))
    }
}