package com.willfp.ecoskills.data

import com.willfp.ecoskills.getTotalSkillLevel
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

class LeaderboardHandler {
    companion object {
        val sortedLeaderboard = mutableListOf<OfflinePlayer>()

        fun getPage(page: Int): MutableMap<Int, OfflinePlayer> {
            val maxPage = ceil(sortedLeaderboard.size / 10.0).toInt()
            val finalPage = max(1, min(page, maxPage))
            Bukkit.getLogger().info("page final $finalPage")

            val startIndex = (finalPage - 1) * 10
            val endIndex = min(startIndex + 9, sortedLeaderboard.size - 1)

            val players = sortedLeaderboard.subList(startIndex, endIndex)
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