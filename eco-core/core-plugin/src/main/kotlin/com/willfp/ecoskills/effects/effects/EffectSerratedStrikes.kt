package com.willfp.ecoskills.effects.effects

import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.scheduling.RunnableTask
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import com.willfp.ecoskills.tryAsPlayer
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
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
        if (this.config.getStrings("disabled-in-worlds").contains(event.entity.world.name)) {
            return
        }

        val player = event.damager.tryAsPlayer() ?: return
        val victim = if (event.entity is LivingEntity) event.entity as LivingEntity else return

        if (player.uniqueId == victim.uniqueId) {
            return
        }

        if (victim is HumanEntity && victim.isBlocking) {
            return
        }

        if (victim.hasMetadata("serratedStrikes") &&
            this.config.getBool("prevent-multiple-bleeding")) {
            return
        }

        if (!this.checkConditions(player)) {
            return
        }

        if (!AntigriefManager.canInjure(player, victim)) {
            return
        }

        val level = player.getEffectLevel(this)

        if (NumberUtils.randFloat(0.0, 100.0) >= config.getDouble("chance-per-level") * level) {
            return
        }

        val bleedDamage = config.getDouble("bleed-tick-damage")

        val bleedCount = config.getInt("bleed-ticks")

        var currentBleedCount = 0

        victim.setMetadata("serratedStrikes", plugin.metadataValueFactory.create(1))

        plugin.runnableFactory.create { bukkitRunnable: RunnableTask ->
            currentBleedCount++
            victim.damage(bleedDamage)
            if (currentBleedCount >= bleedCount) {
                victim.removeMetadata("serratedStrikes", plugin)
                bukkitRunnable.cancel()
                return@create
            }
        }.runTaskTimer(config.getInt("bleed-tick-spacing").toLong(), config.getInt("bleed-tick-spacing").toLong())
    }
}