package com.willfp.ecoskills.effects.effects

import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import com.willfp.ecoskills.tryAsPlayer
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector


class EffectDazzle : Effect(
    "dazzle"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%chance%", NumberUtils.format(config.getDouble("chance-per-level") * level))
            .replace("%seconds%", NumberUtils.format(((config.getInt("ticks-per-level") * level) + config.getInt("base-ticks")) / 20.0))
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
        if (this.config.getStrings("disabled-in-worlds").contains(event.entity.world.name)) {
            return
        }

        val player = event.damager.tryAsPlayer() ?: return
        val victim = if (event.entity is LivingEntity) event.entity as LivingEntity else return

        if (!this.checkConditions(player)) {
            return
        }

        val level = player.getEffectLevel(this)

        if (NumberUtils.randFloat(0.0, 100.0) >= config.getDouble("chance-per-level") * level) {
            return
        }

        val duration = (config.getInt("ticks-per-level") * level) + config.getInt("base-ticks")

        victim.velocity = Vector(0, 0, 0)
        victim.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, duration, level))
    }
}