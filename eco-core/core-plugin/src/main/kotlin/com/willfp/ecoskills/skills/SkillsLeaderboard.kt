package com.willfp.ecoskills.skills

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.ecoskills.Levellable
import com.willfp.ecoskills.util.LeaderboardEntry
import org.bukkit.Bukkit
import java.util.UUID
import java.util.concurrent.TimeUnit

object SkillsLeaderboard {
    private var leaderboardCache = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .build<Boolean, Map<Levellable, List<UUID>>> {
            val offlinePlayers = Bukkit.getOfflinePlayers()
            val top = mutableMapOf<Levellable, List<UUID>>()
            for (skill in Skills.values())
                top[skill] = offlinePlayers.sortedByDescending { skill.getActualLevel(it) }.map { it.uniqueId }
            return@build top
        }

    fun getTop(skill: Levellable, position: Int): LeaderboardEntry? {
        require(position > 0) { "Position must be greater than 0" }

        val uuid = leaderboardCache.get(true)[skill]?.getOrNull(position - 1) ?: return null

        val player = Bukkit.getOfflinePlayer(uuid).takeIf { it.hasPlayedBefore() } ?: return null

        return LeaderboardEntry(
            player,
            skill.getActualLevel(player)
        )
    }

    fun getPosition(skill: Levellable, uuid: UUID): Int? {
        val leaderboard = leaderboardCache.get(true)[skill]
        val index = leaderboard?.indexOf(uuid)
        return if (index == -1) null else index?.plus(1)
    }
}