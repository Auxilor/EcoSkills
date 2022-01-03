package com.willfp.ecoskills.stats.stats

import com.willfp.ecoskills.getStatLevel
import com.willfp.ecoskills.stats.Stat
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent

class StatDefense : Stat(
    "defense"
) {
    @EventHandler(priority = EventPriority.LOW)
    fun handle(event: EntityDamageEvent) {
        if (event.entity !is Player) {
            return
        }

        val player = event.entity as Player

        if (this.config.getStrings("disabled-in-worlds").contains(player.world.name)) {
            return
        }

        var multiplier = this.config.getDouble("percent-less-damage-per-level") * player.getStatLevel(this)
        multiplier /= 100
        multiplier += 1

        event.damage = (event.damage) / multiplier
    }
}