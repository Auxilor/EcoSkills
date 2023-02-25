package com.willfp.ecoskills.stats.stats

import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.toNiceString
import com.willfp.ecoskills.getStatLevel
import com.willfp.ecoskills.isCrit
import com.willfp.ecoskills.isStatEnabled
import com.willfp.ecoskills.stats.Stat
import com.willfp.ecoskills.stats.Stats
import com.willfp.ecoskills.tryAsPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.lang.Double.min

class StatCritChance : Stat(
    "crit_chance"
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

        if (this.config.getStrings("disabled-in-worlds").contains(player.world.name)
            || Stats.CRIT_DAMAGE.config.getStrings("disabled-in-worlds").contains(player.world.name)) {
            return
        }

        if (NumberUtils.randFloat(0.0, 100.0) >= (this.config.getDouble("chance-per-level") * player.getStatLevel(this))) {
            return
        }

        var multiplier = Stats.CRIT_DAMAGE.config.getDouble("percent-more-damage-per-level") * player.getStatLevel(Stats.CRIT_DAMAGE)
        multiplier += Stats.CRIT_DAMAGE.config.getDouble("base-percent-more")
        multiplier /= 100
        multiplier += 1

        event.isCrit = true

        event.damage = (event.damage) * multiplier
    }
}