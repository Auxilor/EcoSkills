package com.willfp.ecoskills.data

import com.willfp.eco.core.EcoPlugin
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.PlayerHelper
import com.willfp.ecoskills.getTotalSkillLevel
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class LeaderboardRunnable (
    private val plugin: EcoSkillsPlugin
) : Runnable {
    override fun run() {
        val uuidStrings = plugin.dataYml.getSubsectionOrNull("player")?.getKeys(false) ?: ArrayList()
        val uuids = uuidStrings.stream().map { s -> UUID.fromString(s) }.toList()

        val temp = HashMap<OfflinePlayer, Int>()
        val top = ArrayList<OfflinePlayer>()

        for (uuid in uuids) {
            val player = Bukkit.getOfflinePlayer(uuid)
            temp[player] = 10000 - player.getTotalSkillLevel()
        }

        val temp2 = temp.toList().sortedBy { (_, value) -> value }.toMap()

        for (entry in temp2) {
            top.add(entry.key)
        }

        sortedLeaderboard.clear()
        sortedLeaderboard.addAll(top)
    }

    companion object {
        val sortedLeaderboard = ArrayList<OfflinePlayer>()
    }
}