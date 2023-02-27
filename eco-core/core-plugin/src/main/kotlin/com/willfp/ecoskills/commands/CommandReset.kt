@file:Suppress("DEPRECATION")

package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.util.StringUtils
import com.willfp.ecoskills.resetSkill
import com.willfp.ecoskills.resetSkills
import com.willfp.ecoskills.setStatLevel
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stat
import com.willfp.ecoskills.stats.Stats
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class CommandReset(plugin: EcoPlugin) :
    Subcommand(
        plugin,
        "reset",
        "ecoskills.command.reset",
        false
    ) {

    override fun onExecute(sender: CommandSender, args: List<String>) {
        if (args.isEmpty()) {
            sender.sendMessage(plugin.langYml.getMessage("requires-player"))
            return
        }

        if (args[0].equals("all", ignoreCase = true)) {
            sender.sendMessage(plugin.langYml.getMessage("resetting-all-players"))
            Bukkit.getOfflinePlayers().forEach { it.resetSkills() }
            sender.sendMessage(plugin.langYml.getMessage("reset-all-players"))
        } else {
            val player = Bukkit.getOfflinePlayer(args[0])
            if (!player.hasPlayedBefore()) {
                sender.sendMessage(plugin.langYml.getMessage("invalid-player"))
                return
            }

            if (args.size == 2) {
                when (val obj = Skills.getByID(args[1].lowercase()) ?: Stats.getByID(args[1].lowercase())) {
                    is Skill -> {
                        sender.sendMessage(
                            plugin.langYml.getMessage(
                                "reset-player-skill",
                                StringUtils.FormatOption.WITHOUT_PLACEHOLDERS
                            )
                                .replace("%player%", args[0])
                                .replace("%skill%", obj.name)
                        )
                        player.resetSkill(obj)
                    }

                    is Stat -> {
                        sender.sendMessage(
                            plugin.langYml.getMessage(
                                "reset-player-stat",
                                StringUtils.FormatOption.WITHOUT_PLACEHOLDERS
                            )
                                .replace("%player%", args[0])
                                .replace("%skill%", obj.name)
                        )
                        player.setStatLevel(obj, 0)
                    }

                    else -> sender.sendMessage(plugin.langYml.getMessage("invalid-skill-stat"))
                }
                return
            }

            sender.sendMessage(plugin.langYml.getMessage("reset-player"))
            player.resetSkills()
        }
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1) {
            StringUtil.copyPartialMatches(
                args[0],
                Bukkit.getOnlinePlayers().map { player -> player.name }.toMutableList(),
                completions
            )
            return completions
        }

        if (args.size == 2) {
            StringUtil.copyPartialMatches(
                args[1],
                TabCompleteHelper.SKILL_NAMES + TabCompleteHelper.STAT_NAMES,
                completions
            )
            return completions
        }

        return emptyList()
    }
}