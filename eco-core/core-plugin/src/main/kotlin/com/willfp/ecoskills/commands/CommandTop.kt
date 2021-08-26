package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.CommandHandler
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.util.StringUtils
import com.willfp.ecoskills.data.LeaderboardHandler
import com.willfp.ecoskills.getTotalSkillLevel
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.math.ceil


class CommandTop(plugin: EcoPlugin) :
    Subcommand(
        plugin,
        "top",
        "ecoskills.command.top",
        false
    ) {
    override fun getHandler(): CommandHandler {
        return CommandHandler { sender: CommandSender, args: List<String> ->
            var page = args.firstOrNull()?.toIntOrNull() ?: 1

            val top = LeaderboardHandler.sortedLeaderboard

            val maxPage = ceil(top.size / 10.0).toInt()
            if (maxPage < page) {
                page = maxPage
            }

            if (page <= 0) {
                page = 1
            }

            val pagePlayers = mutableListOf<OfflinePlayer>()

            val start = (page - 1) * 10
            val end = start + 9

            for (i in start..end) {
                if (i > top.size - 1) {
                    break
                }

                pagePlayers.add(top[i])
            }

            val messages = plugin.langYml.getStrings("top", false)
            val lines = mutableListOf<String>()

            val useDisplayName = plugin.configYml.getBool("commands.top.use-display-name")

            var rank = start + 1

            for (player in pagePlayers) {
                var line = plugin.langYml.getString("top-line-format", false)
                    .replace("%rank%", rank.toString())
                    .replace("%level%", player.getTotalSkillLevel().toString())

                var name = player.name!!

                if (useDisplayName && player is Player) {
                    name = player.displayName
                }

                line = line.replace("%playername%", name)

                lines.add(line)

                rank++
            }

            val linesIndex = messages.indexOf("%lines%")
            if (linesIndex != -1) {
                messages.removeAt(linesIndex)
                messages.addAll(linesIndex, lines)
            }

            for (message in messages) {
                sender.sendMessage(StringUtils.format(message))
            }
        }
    }
}