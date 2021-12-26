package com.willfp.ecoskills.data

import com.willfp.ecoskills.getSkillLevel
import com.willfp.ecoskills.getTotalSkillLevel
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.skills.Skills
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.Collections
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

class LeaderboardHandler {
    companion object {
        private val sortedLeaderboard = Collections.synchronizedList(mutableListOf<OfflinePlayer>())
        private val skillLeaderboards = Collections.synchronizedMap(mutableMapOf<Skill, List<OfflinePlayer>>())

        fun getPage(page: Int, skill: Skill? = null): MutableMap<Int, OfflinePlayer> {
            val selectedLeaderboard = if (skill == null) sortedLeaderboard else skillLeaderboards[skill]!!

            val maxPage = ceil(selectedLeaderboard.size / 10.0).toInt()
            val finalPage = max(1, min(page, maxPage))

            val startIndex = (finalPage - 1) * 10
            val endIndex = min(startIndex + 10, selectedLeaderboard.size - 1)

            val players = selectedLeaderboard.subList(startIndex, endIndex)
            val withRank = mutableMapOf<Int, OfflinePlayer>()

            var rank = startIndex + 1
            for (player in players) {
                withRank[rank] = player
                rank++
            }

            return withRank
        }
    }

    class Runnable : java.lang.Runnable {
        override fun run() {
            val players = Bukkit.getOfflinePlayers()

            for (skill in Skills.values()) {
                skillLeaderboards[skill] = players.sortedByDescending { it.getSkillLevel(skill) }
            }

            sortedLeaderboard.clear()
            sortedLeaderboard.addAll(players.toList().sortedByDescending { it.getTotalSkillLevel() })
        }
    }
}