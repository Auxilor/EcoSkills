package com.willfp.ecoskills.stats.stats

import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.getStatLevel
import com.willfp.ecoskills.stats.Stat
import com.willfp.ecoskills.tryAsPlayer
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent

class StatFerocity : Stat(
    "ferocity"
) {
    @EventHandler(priority = EventPriority.LOW)
    fun handle(event: EntityDamageByEntityEvent) {
        val player = event.damager.tryAsPlayer() ?: return

        if (this.config.getStrings("disabled-in-worlds").contains(player.world.name)) {
            return
        }

        val victim = if (event.entity is LivingEntity) event.entity as LivingEntity else return

        if (victim.hasMetadata("ferocity")) {
            return
        }

        val level = player.getStatLevel(this)

        if (NumberUtils.randFloat(0.0, 100.0) >= this.config.getDouble("chance-per-level") * level) {
            return
        }

        this.plugin.scheduler.run {
            victim.setMetadata("ferocity", plugin.metadataValueFactory.create(true))
            victim.noDamageTicks = 0
            victim.damage(event.damage, player)
        }
    }
}