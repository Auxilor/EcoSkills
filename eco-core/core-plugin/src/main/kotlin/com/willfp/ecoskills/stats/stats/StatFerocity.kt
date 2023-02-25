package com.willfp.ecoskills.stats.stats

import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.toNiceString
import com.willfp.ecoskills.getStatLevel
import com.willfp.ecoskills.isStatEnabled
import com.willfp.ecoskills.stats.Stat
import com.willfp.ecoskills.tryAsPlayer
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.lang.Double.min

class StatFerocity : Stat(
    "ferocity"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace(
            "%chance%",
            (min(this.config.getDouble("chance-per-level") * level, 100.0)).toNiceString()
        )
    }

    @EventHandler(priority = EventPriority.LOW)
    fun handle(event: EntityDamageByEntityEvent) {
        val player = event.damager.tryAsPlayer() ?: return


        if (!player.isStatEnabled(this)) {
            return
        }

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