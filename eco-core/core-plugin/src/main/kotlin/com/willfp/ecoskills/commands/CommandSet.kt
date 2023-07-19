package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.util.StringUtils
import com.willfp.eco.util.formatEco
import com.willfp.ecoskills.api.setBaseStatLevel
import com.willfp.ecoskills.api.setSkillLevel
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stat
import com.willfp.ecoskills.stats.Stats
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class CommandSet(plugin: EcoPlugin) :
    Subcommand(
        plugin,
        "set",
        "ecoskills.command.set",
        false
    ) {

    override fun onExecute(sender: CommandSender, args: MutableList<String>) {
        val player = notifyPlayerRequired(args.getOrNull(0), "invalid-player")

        val obj = notifyNull(
            Skills.getByID(args.getOrNull(1)) ?: Stats.getByID(args.getOrNull(1)),
            "invalid-skill-stat"
        )

        val level = notifyNull(args.getOrNull(2)?.toIntOrNull(), "invalid-amount")

        val key = when (obj) {
            is Skill -> {
                player.setSkillLevel(obj, level)
                "set-skill-level"
            }

            is Stat -> {
                player.setBaseStatLevel(obj, level)
                "set-stat"
            }

            else -> ""
        }

        sender.sendMessage(
            this.plugin.langYml.getMessage(key, StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                .replace("%player%", player.name)
                .replace("%amount%", level.toString())
                .replace("%obj%", obj.name)
                .formatEco()
        )
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1) {
            StringUtil.copyPartialMatches(
                args[0],
                Bukkit.getOnlinePlayers().map { player -> player.name }.toCollection(ArrayList()),
                completions
            )
            return completions
        }

        if (args.size == 2) {
            StringUtil.copyPartialMatches(
                args[1],
                (Skills.values() union Stats.values()).map { it.id },
                completions
            )
            return completions
        }

        if (args.size == 3) {
            StringUtil.copyPartialMatches(
                args[2],
                listOf(1, 2, 5, 10, 100).map { it.toString() },
                completions
            )
            return completions
        }

        return emptyList()
    }

}