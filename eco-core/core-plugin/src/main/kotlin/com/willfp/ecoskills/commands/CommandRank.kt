package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.CommandHandler
import com.willfp.eco.core.command.TabCompleteHandler
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.util.StringUtils
import com.willfp.ecoskills.data.LeaderboardHandler
import com.willfp.ecoskills.getSkillLevel
import com.willfp.ecoskills.getStatLevel
import com.willfp.ecoskills.getTotalSkillLevel
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stats
import com.willfp.ecoskills.util.TabCompleteHelper
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil


class CommandRank(plugin: EcoPlugin) :
        Subcommand(
                plugin,
                "rank",
                "ecoskills.command.rank",
                false
        ) {
    override fun getHandler(): CommandHandler {
        return CommandHandler { sender: CommandSender, args: List<String> ->
            if (args.isEmpty()) {
                sender.sendMessage(plugin.langYml.getMessage("requires-skill-stat"))
                return@CommandHandler
            }

            val page = args[0].toIntOrNull() ?: 1

            if (page < 1 || page > LeaderboardHandler.totalPages()) {
                sender.sendMessage(
                        plugin.langYml.getMessage("invalid-page-number")
                        .replace("%min_page_number%", "1")
                        .replace("%max_page_number%", LeaderboardHandler.totalPages().toString())
                )
                return@CommandHandler
            }

            val currentSkill = Skills.getByID(args[0].lowercase())


            if (currentSkill == null) {
                sender.sendMessage(plugin.langYml.getMessage("invalid-skill-stat"))
                return@CommandHandler
            }

            val top = LeaderboardHandler.getPage(page, skill = currentSkill)


//            if (currentSkill != null) {
//                val top = LeaderboardHandler.getPage(page, currentSkill)
//            }
//            if (currentStat != null) {
//                val top = LeaderboardHandler.getPage(page, currentSkill)
//            }


            val messages = plugin.langYml.getStrings("top", false).toMutableList()
            val lines = mutableListOf<String>()

            val useDisplayName = plugin.configYml.getBool("commands.rank.use-display-name")

            for ((rank, player) in top) {
                var line = plugin.langYml.getString("top-line-format", false)
                        .replace("%rank%", rank.toString())
                        .replace("%level%", player.getSkillLevel(currentSkill).toString())


                var name = player.name!!

                if (useDisplayName && player is Player) {
                    name = player.displayName
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
    }

    override fun getTabCompleter(): TabCompleteHandler {
        return TabCompleteHandler { _, args ->
            val completions = mutableListOf<String>()

            if (args.size == 1) {
                StringUtil.copyPartialMatches(
                        args[0],
                        TabCompleteHelper.SKILL_NAMES,
                        completions
                )
                return@TabCompleteHandler completions
            }
            return@TabCompleteHandler emptyList<String>()
        }
    }

}