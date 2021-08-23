package com.willfp.ecoskills.stats.stats

import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.getStatLevel
import com.willfp.ecoskills.isCrit
import com.willfp.ecoskills.stats.Stat
import com.willfp.ecoskills.stats.Stats
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent

class StatFerocity : Stat(
    "ferocity"
) {
    @EventHandler(priority = EventPriority.LOW)
    fun handle(event: EntityDamageByEntityEvent) {
        var player = event.damager
        val victim = event.entity
        val entity = event.damager

        if (victim.hasMetadata("ferocity")) {
            return
        }

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

        val level = player.getStatLevel(this)

        if (NumberUtils.randFloat(0.0, 100.0) >= this.config.getDouble("chance-per-level") * level) {
            return
        }

        this.plugin.run {
            victim.setMetadata("ferocity", plugin.metadataValueFactory.create(true))
            victim.noDamageTicks = 0
            victim.damage(event.damage, entity)
        }
    }
}