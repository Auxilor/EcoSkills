package com.willfp.ecoskills.effects.effects

import com.willfp.eco.core.scheduling.RunnableTask
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import com.willfp.ecoskills.tryAsPlayer
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent


class EffectSerratedStrikes : Effect(
    "serrated_strikes"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%chance%", NumberUtils.format(config.getDouble("chance-per-level") * level))
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
        val player = event.damager.tryAsPlayer() ?: return
        val victim = if (event.entity is LivingEntity) event.entity as LivingEntity else return

        val level = player.getEffectLevel(this)

        if (NumberUtils.randFloat(0.0, 100.0) >= config.getDouble("chance-per-level") * level) {
            return
        }

        val bleedDamage = config.getDouble("bleed-tick-damage")

        var bleedCount = config.getInt("bleed-ticks")
        bleedCount *= level

        var currentBleedCount = 0

        plugin.runnableFactory.create { bukkitRunnable: RunnableTask ->
            currentBleedCount++
            victim.damage(bleedDamage)
            if (currentBleedCount >= bleedCount) {
                bukkitRunnable.cancel()
                return@create
            }
        }.runTaskTimer(config.getInt("bleed-tick-spacing").toLong(), config.getInt("bleed-tick-spacing").toLong())
    }
}