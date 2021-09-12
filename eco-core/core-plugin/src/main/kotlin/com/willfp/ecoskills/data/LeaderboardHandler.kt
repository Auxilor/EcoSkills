package com.willfp.ecoskills.data

import com.willfp.ecoskills.getSkillLevel
import com.willfp.ecoskills.getTotalSkillLevel
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.skills.Skills
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

class LeaderboardHandler {
    companion object {
        private val sortedLeaderboard = mutableListOf<OfflinePlayer>()
        private val skillLeaderboards = mutableMapOf<Skill, MutableList<OfflinePlayer>>()

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
            for (skill in Skills.values()) {
                val temp = mutableMapOf<OfflinePlayer, Int>()
                val top = mutableListOf<OfflinePlayer>()

                for (player in Bukkit.getOfflinePlayers()) {
                    temp[player] = 10000 - player.getSkillLevel(skill)
                }

                val temp2 = temp.toList().sortedBy { (_, value) -> value }.toMap()
                for (key in temp2.keys) {
                    top.add(key)
                }

                skillLeaderboards[skill]?.clear()
                skillLeaderboards[skill] = top
            }

            val temp = mutableMapOf<OfflinePlayer, Int>()
            val top = mutableListOf<OfflinePlayer>()

            for (player in Bukkit.getOfflinePlayers()) {
                temp[player] = 10000 - player.getTotalSkillLevel()
            }

            val temp2 = temp.toList().sortedBy { (_, value) -> value }.toMap()
            for (key in temp2.keys) {
                top.add(key)
            }

            sortedLeaderboard.clear()
            sortedLeaderboard.addAll(top)
        }
    }
}