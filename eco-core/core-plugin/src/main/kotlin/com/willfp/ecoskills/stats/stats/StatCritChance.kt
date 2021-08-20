package com.willfp.ecoskills.stats.stats

import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.getStatLevel
import com.willfp.ecoskills.stats.Stat
import com.willfp.ecoskills.stats.Stats
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent

class StatCritChance : Stat(
    "crit_chance"
) {
    @EventHandler(priority = EventPriority.LOW)
    fun handle(event: EntityDamageByEntityEvent) {
        var player = event.damager

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

        if (NumberUtils.randFloat(0.0, 100.0) >= (this.config.getDouble("chance-per-level") * player.getStatLevel(this))) {
            return
        }

        var multiplier = Stats.CRIT_DAMAGE.config.getDouble("percent-more-damage-per-level") * player.getStatLevel(Stats.CRIT_DAMAGE)
        multiplier += Stats.CRIT_DAMAGE.config.getDouble("base-percent-more")
        multiplier /= 100
        multiplier += 1

        event.damage = (event.damage) * multiplier
    }
}