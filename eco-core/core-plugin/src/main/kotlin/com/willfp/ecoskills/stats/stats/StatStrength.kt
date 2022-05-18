package com.willfp.ecoskills.stats.stats

import com.willfp.eco.util.toNiceString
import com.willfp.ecoskills.getStatLevel
import com.willfp.ecoskills.stats.Stat
import com.willfp.ecoskills.tryAsPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent

class StatStrength : Stat(
    "strength"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace(
            "%percent_more_damage%",
            (this.config.getDouble("percent-more-damage-per-level") * level).toNiceString()
        )
    }

    @EventHandler(priority = EventPriority.LOW)
    fun handle(event: EntityDamageByEntityEvent) {
        val player = event.damager.tryAsPlayer() ?: return

        if (this.config.getStrings("disabled-in-worlds").contains(player.world.name)) {
            return
        }

        var multiplier = this.config.getDouble("percent-more-damage-per-level") * player.getStatLevel(this)
        multiplier /= 100
        multiplier += 1

        event.damage = (event.damage) * multiplier
    }
}