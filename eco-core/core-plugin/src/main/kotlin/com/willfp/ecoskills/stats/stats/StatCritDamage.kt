package com.willfp.ecoskills.stats.stats

import com.willfp.eco.util.toNiceString
import com.willfp.ecoskills.stats.Stat

class StatCritDamage : Stat(
    "crit_damage"
) {
    override fun formatDescription(string: String, level: Int): String {
        var multiplier = this.config.getDouble("percent-more-damage-per-level") * level
        multiplier += this.config.getDouble("base-percent-more")

        return string.replace(
            "%percent_more_damage%",
            multiplier.toNiceString()
        )
    }
}