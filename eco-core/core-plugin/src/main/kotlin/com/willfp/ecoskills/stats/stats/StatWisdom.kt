package com.willfp.ecoskills.stats.stats

import com.willfp.eco.core.events.NaturalExpGainEvent
import com.willfp.eco.util.toNiceString
import com.willfp.ecoskills.getStatLevel
import com.willfp.ecoskills.stats.Stat
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import kotlin.math.ceil

class StatWisdom : Stat(
    "wisdom"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace(
            "%percent_more_xp%",
            (this.config.getDouble("percent-more-xp-gain") * level).toNiceString()
        )
    }

    @EventHandler(priority = EventPriority.LOW)
    fun handle(event: NaturalExpGainEvent) {
        val player = event.expChangeEvent.player

        if (this.config.getStrings("disabled-in-worlds").contains(player.world.name)) {
            return
        }

        var multiplier = this.config.getDouble("percent-more-xp-gain") * player.getStatLevel(this)
        multiplier /= 100
        multiplier += 1

        event.expChangeEvent.amount = ceil((event.expChangeEvent.amount * multiplier)).toInt()
    }
}