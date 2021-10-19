package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.CommandHandler
import com.willfp.eco.core.command.TabCompleteHandler
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.ecoskills.getBaseStatLevel
import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.setStatLevel
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stat
import com.willfp.ecoskills.stats.Stats
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil


class CommandGive(plugin: EcoPlugin) :
    Subcommand(
        plugin,
        "give",
        "ecoskills.command.give",
        false
    ) {
    override fun getHandler(): CommandHandler {
        return CommandHandler { sender: CommandSender, args: List<String> ->
            if (args.isEmpty()) {
                sender.sendMessage(plugin.langYml.getMessage("requires-player"))
                return@CommandHandler
            }

            if (args.size == 1) {
                sender.sendMessage(plugin.langYml.getMessage("requires-skill-stat"))
                return@CommandHandler
            }

            if (args.size == 2) {
                sender.sendMessage(plugin.langYml.getMessage("requires-amount"))
                return@CommandHandler
            }

            val player = Bukkit.getPlayer(args[0])
            if (player == null) {
                sender.sendMessage(plugin.langYml.getMessage("invalid-player"))
                return@CommandHandler
            }

            val obj = Skills.getByID(args[1].lowercase()) ?: Stats.getByID(args[1].lowercase())

            if (obj == null) {
                sender.sendMessage(plugin.langYml.getMessage("invalid-skill-stat"))
                return@CommandHandler
            }

            val amount = args[2].toIntOrNull()

            if (amount == null) {
                sender.sendMessage(plugin.langYml.getMessage("invalid-amount"))
                return@CommandHandler
            }

            if (obj is Skill) {
                player.giveSkillExperience(obj, amount.toDouble(), noMultiply = true)
                player.sendMessage(
                    this.plugin.langYml.getMessage("gave-skill-xp")
                        .replace("%player%", player.name)
                        .replace("%amount%", amount.toString())
                        .replace("%skill%", obj.name)
                )
                return@CommandHandler
            }

            if (obj is Stat) {
                player.setStatLevel(obj, player.getBaseStatLevel(obj) + amount)
                sender.sendMessage(
                    this.plugin.langYml.getMessage("gave-stat")
                        .replace("%player%", player.name)
                        .replace("%amount%", amount.toString())
                        .replace("%stat%", obj.name)
                )
                return@CommandHandler
            }
        }
    }

    override fun getTabCompleter(): TabCompleteHandler {
        return TabCompleteHandler { _, args ->
            val completions = mutableListOf<String>()

            if (args.size == 1) {
                StringUtil.copyPartialMatches(
                    args[0],
                    Bukkit.getOnlinePlayers().map { player -> player.name }.toCollection(ArrayList()),
                    completions
                )
                return@TabCompleteHandler completions
            }

            if (args.size == 2) {
                StringUtil.copyPartialMatches(
                    args[1],
                    TabCompleteHelper.SKILL_NAMES union TabCompleteHelper.STAT_NAMES,
                    completions
                )
                return@TabCompleteHandler completions
            }

            if (args.size == 3) {
                StringUtil.copyPartialMatches(
                    args[2],
                    TabCompleteHelper.AMOUNTS,
                    completions
                )
                return@TabCompleteHandler completions
            }

            return@TabCompleteHandler emptyList<String>()
        }
    }
}