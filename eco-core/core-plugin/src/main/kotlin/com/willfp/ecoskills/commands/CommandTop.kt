package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.util.PlayerUtils
import com.willfp.eco.util.StringUtils
import com.willfp.ecoskills.data.LeaderboardHandler
import com.willfp.ecoskills.getTotalSkillLevel
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil


class CommandTop(plugin: EcoPlugin) :
    Subcommand(
        plugin,
        "top",
        "ecoskills.command.top",
        false
    ) {

    override fun onExecute(sender: CommandSender, args: List<String>) {
        val page = args.firstOrNull()?.toIntOrNull() ?: 1
        val top = LeaderboardHandler.getPage(page)

        val messages = plugin.langYml.getStrings("top.format")
        val lines = mutableListOf<String>()

        val useDisplayName = plugin.configYml.getBool("commands.top.use-display-name")

        for ((rank, player) in top) {
            var line = plugin.langYml.getString("top-line-format")
                .replace("%rank%", rank.toString())
                .replace("%level%", player.getTotalSkillLevel().toString())

            var name: String

            @Suppress("SENSELESS_COMPARISON")
            if (player == null) {
                name = "Unknown Player"
            } else {
                name = player.name ?: "Unknown Player"

                if (useDisplayName) {
                    name = PlayerUtils.getSavedDisplayName(player)
                }
            }

            line = line.replace("%playername%", name)

            lines.add(line)
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

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1) {
            StringUtil.copyPartialMatches(
                args[0],
                TabCompleteHelper.NUMBERS,
                completions
            )
            return completions
        }

        return emptyList()
    }
}