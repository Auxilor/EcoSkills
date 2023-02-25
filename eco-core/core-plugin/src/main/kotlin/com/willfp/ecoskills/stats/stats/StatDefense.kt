package com.willfp.ecoskills.stats.stats

import com.willfp.eco.util.toNiceString
import com.willfp.ecoskills.getStatLevel
import com.willfp.ecoskills.isStatEnabled
import com.willfp.ecoskills.stats.Stat
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent

class StatDefense : Stat(
    "defense"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace(
            "%percent_less_damage%",
            (this.config.getDouble("percent-less-damage-per-level") * level).toNiceString()
        )
    }

    @EventHandler(priority = EventPriority.LOW)
    fun handle(event: EntityDamageEvent) {
        if (event.entity !is Player) {
            return
        }

        val player = event.entity as Player


        if (!player.isStatEnabled(this)) {
            return
        }

        if (this.config.getStrings("disabled-in-worlds").contains(player.world.name)) {
            return
        }

        var multiplier = this.config.getDouble("percent-less-damage-per-level") * player.getStatLevel(this)
        multiplier /= 100
        multiplier += 1

        event.damage = (event.damage) / multiplier
    }
}