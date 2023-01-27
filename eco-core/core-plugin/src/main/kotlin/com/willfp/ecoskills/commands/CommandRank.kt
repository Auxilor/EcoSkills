package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.util.PlayerUtils
import com.willfp.eco.util.StringUtils
import com.willfp.ecoskills.data.LeaderboardHandler
import com.willfp.ecoskills.getSkillLevel
import com.willfp.ecoskills.skills.Skills
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil


class CommandRank(plugin: EcoPlugin) :
    Subcommand(
        plugin,
        "rank",
        "ecoskills.command.rank",
        false
    ) {

    override fun onExecute(sender: CommandSender, args: List<String>) {
        if (args.isEmpty()) {
            sender.sendMessage(plugin.langYml.getMessage("requires-skill"))
            return
        }

        val skill = Skills.getByID(args[0].lowercase())

        if (skill == null || !skill.enabled) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-skill"))
            return
        }

        val page = if (args.size < 2) 1 else args[1].toIntOrNull() ?: 1

        val top = LeaderboardHandler.getPage(page, skill)

        val messages = plugin.langYml.getStrings("top.format")
        val lines = mutableListOf<String>()

        val useDisplayName = plugin.configYml.getBool("commands.top.use-display-name")

        for ((rank, player) in top) {
            var line = plugin.langYml.getString("top-line-format")
                .replace("%rank%", rank.toString())
                .replace("%level%", player.getSkillLevel(skill).toString())


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
                TabCompleteHelper.SKILL_NAMES,
                completions
            )
            return completions
        }

        if (args.size == 2) {
            StringUtil.copyPartialMatches(
                args[1],
                TabCompleteHelper.NUMBERS,
                completions
            )
            return completions
        }

        return emptyList()
    }
}