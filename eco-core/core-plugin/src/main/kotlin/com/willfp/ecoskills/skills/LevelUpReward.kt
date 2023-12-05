package com.willfp.ecoskills.skills

import com.willfp.ecoskills.Levellable
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.effects.effects
import com.willfp.ecoskills.stats.Stat
import com.willfp.ecoskills.stats.stats
import org.bukkit.OfflinePlayer

class LevelUpReward(
    val reward: Levellable,
    private val levels: Int,
    private val startLevel: Int?,
    private val endLevel: Int?,
    private val every: Int?
) {
    fun getCumulativeLevels(level: Int): Int {
        var sum = 0

        for (skillLevel in (1..level)) {
            if (startLevel != null && skillLevel < startLevel) {
                continue
            }

            if (endLevel != null && skillLevel > endLevel) {
                continue
            }

            if (every != null) {
                if (endLevel != null) {
                    val num = ((startLevel ?: 1)..endLevel).indexOf(skillLevel) + 1
                    if (num % every != 0) {
                        continue
                    }
                } else if ((skillLevel - (startLevel ?: 0)) % every != 0) {
                    continue
                }
            }

            sum += levels
        }

        return sum
    }

    fun giveTo(player: OfflinePlayer, level: Int) {
        if (startLevel != null && level < startLevel) {
            return
        }

        if (endLevel != null && level > endLevel) {
            return
        }

        if (every != null) {
            if (endLevel != null) {
                val num = ((startLevel ?: 1)..endLevel).indexOf(level) + 1
                if (num % every != 0) {
                    return
                }
            } else if ((level - (startLevel ?: 0)) % every != 0) {
                return
            }
        }

        when (reward) {
            is Effect -> player.effects[reward] += levels
            is Stat -> player.stats[reward] += levels
        }
    }
}
