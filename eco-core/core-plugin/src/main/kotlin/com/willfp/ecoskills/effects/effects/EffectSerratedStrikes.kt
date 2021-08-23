package com.willfp.ecoskills.effects.effects

import com.willfp.eco.core.scheduling.RunnableTask
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.util.concurrent.atomic.AtomicInteger


class EffectSerratedStrikes : Effect(
    "serrated_strikes"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%chance%", NumberUtils.format(config.getDouble("chance-per-level") * level))
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
        val player = event.damager
        val victim = event.entity

        if (player !is Player) {
            return
        }

        if (victim !is LivingEntity) {
            return
        }

        val level = player.getEffectLevel(this)

        if (NumberUtils.randFloat(0.0, 100.0) >= config.getDouble("chance-per-level") * level) {
            return
        }

        val bleedDamage = config.getDouble("bleed-tick-damage")

        var bleedCount = config.getInt("bleed-ticks")
        bleedCount *= level
        val finalBleedCount = bleedCount

        val currentBleedCount = AtomicInteger(0)

        plugin.runnableFactory.create { bukkitRunnable: RunnableTask ->
            currentBleedCount.addAndGet(1)
            victim.damage(bleedDamage)
            if (currentBleedCount.get() >= finalBleedCount) {
                bukkitRunnable.cancel()
            }
        }.runTaskTimer(config.getInt("bleed-tick-spacing").toLong(), config.getInt("bleed-tick-spacing").toLong())
    }
}