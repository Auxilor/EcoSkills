package com.willfp.ecoskills.data

import com.willfp.ecoskills.getSkillLevel
import com.willfp.ecoskills.getStatLevel
import com.willfp.ecoskills.getTotalSkillLevel
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stats
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

class LeaderboardHandler {
    companion object {
        val sortedLeaderboard = mutableListOf<OfflinePlayer>()
        val sortedLeaderboardPerSkillStat = mutableMapOf<String, MutableList<OfflinePlayer>>()

        fun getPage(page: Int, skill: Skill? = null): MutableMap<Int, OfflinePlayer> {
            var selectedLeaderboard = sortedLeaderboard

            if (skill is Skill) {
                selectedLeaderboard = sortedLeaderboardPerSkillStat.getOrDefault(skill.toString(), mutableListOf<OfflinePlayer>())
            }

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

        fun totalPages(): Int {
            return ceil(sortedLeaderboard.size / 10.0).toInt()
        }
    }

    class Runnable : java.lang.Runnable {
        override fun run() {
            val playerScoresTop = mutableMapOf<OfflinePlayer, Int>()
            val top = mutableListOf<OfflinePlayer>()
            val playerScoresSkillsTop = mutableMapOf<String, MutableList<OfflinePlayer>>()
            val playerScoresSkills = mutableMapOf<String, MutableMap<OfflinePlayer, Int>>()

            for (player in Bukkit.getOfflinePlayers()) {
                playerScoresTop[player] = 10000 - player.getTotalSkillLevel()
                for (skill in Skills.values()) {
                    val skillMap = playerScoresSkills.getOrDefault(skill.toString(), mutableMapOf<OfflinePlayer, Int>())
                    skillMap[player] = 10000 - player.getSkillLevel(skill)

                    playerScoresSkills.put(skill.toString(), skillMap)
                }
            }

            val playerRankedTop = playerScoresTop.toList().sortedBy { (_, value) -> value }.toMap()
            val playerScoresSkillsRanked = mutableMapOf<String, Map<OfflinePlayer, Int>>()

            for (s in playerScoresSkills.keys) {
                playerScoresSkillsRanked.put(s, playerScoresSkills.get(s)!!.toList().sortedBy { (_, value) -> value }.toMap())
            }

//            val playerRankedTop = playerScoresTop.toList().sortedBy { (_, value) -> value }.toMap()
            for (key in playerRankedTop.keys) {
                top.add(key)
            }

            for (key in playerScoresSkillsRanked.keys) {
                val currentList = mutableListOf<OfflinePlayer>()
                for (qey in playerScoresSkillsRanked.get(key)!!) {
                    currentList.add(qey.key)
                }
                playerScoresSkillsTop.put(key, currentList)
            }

            sortedLeaderboard.clear()
            sortedLeaderboard.addAll(top)

            sortedLeaderboardPerSkillStat.clear()
            sortedLeaderboardPerSkillStat.putAll(playerScoresSkillsTop)
        }
    }
}