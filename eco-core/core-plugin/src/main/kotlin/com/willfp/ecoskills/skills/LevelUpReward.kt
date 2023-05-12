package com.willfp.ecoskills.skills

import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.effects.effects
import com.willfp.ecoskills.Levellable
import com.willfp.ecoskills.stats.Stat
import com.willfp.ecoskills.stats.stats
import org.bukkit.OfflinePlayer

class LevelUpReward(
    private val reward: Levellable,
    private val levels: Int,
    private val startLevel: Int?,
    private val endLevel: Int?
) {
    fun giveTo(player: OfflinePlayer, level: Int) {
        if (startLevel != null && level < startLevel) {
            return
        }

        if (endLevel != null && level > endLevel) {
            return
        }

        when (reward) {
            is Effect -> player.effects[reward] += levels
            is Stat -> player.stats[reward] += levels
        }
    }
}
