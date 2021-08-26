package com.willfp.ecoskills.data

import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.getTotalSkillLevel
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

class LeaderboardHandler (
    private val plugin: EcoSkillsPlugin
) {
    companion object {
        val sortedLeaderboard = mutableListOf<OfflinePlayer>()
    }

    class Runnable : java.lang.Runnable {
        override fun run() {
            val temp = mutableMapOf<OfflinePlayer, Int>()
            val top = mutableListOf<OfflinePlayer>()

            for (player in Bukkit.getOfflinePlayers()) {
                temp[player] = 10000 - player.getTotalSkillLevel()
            }

            val temp2 = temp.toList().sortedBy { (_, value) -> value }.toMap()

            for ((k, _) in temp2) {
                top.add(k)
            }

            sortedLeaderboard.clear()
            sortedLeaderboard.addAll(top)
        }
    }
}