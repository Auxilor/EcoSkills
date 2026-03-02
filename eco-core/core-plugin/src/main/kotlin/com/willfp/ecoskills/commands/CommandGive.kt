package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.util.StringUtils
import com.willfp.eco.util.formatEco
import com.willfp.ecoskills.api.getMagic
import com.willfp.ecoskills.api.gainSkillXP
import com.willfp.ecoskills.api.giveBaseStatLevel
import com.willfp.ecoskills.api.giveSkillXP
import com.willfp.ecoskills.api.setMagic
import com.willfp.ecoskills.magic.MagicType
import com.willfp.ecoskills.magic.MagicTypes
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

    override fun onExecute(sender: CommandSender, args: List<String>) {
        val player = notifyPlayerRequired(args.getOrNull(0), "invalid-player")

        val obj = notifyNull(
            Skills.getByID(args.getOrNull(1)) ?: Stats.getByID(args.getOrNull(1)) ?: MagicTypes.getByID(args.getOrNull(1)),
            "invalid-skill-stat-magic"
        )

        val amount = notifyNull(args.getOrNull(2)?.toDoubleOrNull(), "invalid-amount")

        notifyFalse(amount >= 0, "invalid-amount")

        // Get optional showActionBar parameter (default to false for backward compatibility)
        val showActionBar = args.getOrNull(3)?.toBooleanStrictOrNull() ?: false

        val key = when (obj) {
            is Skill -> {
                if (showActionBar) {
                    player.gainSkillXP(obj, amount)
                    "gained-skill-xp"
                } else {
                    player.giveSkillXP(obj, amount)
                    "gave-skill-xp"
                }
            }

            is Stat -> {
                player.giveBaseStatLevel(obj, amount.toInt())
                "gave-stat"
            }

            is MagicType -> {
                val newAmount = player.getMagic(obj) + amount.toInt()
                player.setMagic(obj, newAmount)
                "gave-magic"
            }

            else -> ""
        }

        val objName = when (obj) {
            is Skill -> obj.name
            is Stat -> obj.name
            is MagicType -> obj.name
            else -> "unknown"
        }

        sender.sendMessage(
            this.plugin.langYml.getMessage(key, StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                .replace("%player%", player.name)
                .replace("%amount%", amount.toString())
                .replace("%obj%", objName)
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
                (Skills.values() union Stats.values() union MagicTypes.values()).map { it.id },
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

        if (args.size == 4) {
            StringUtil.copyPartialMatches(
                args[3],
                listOf("true", "false"),
                completions
            )
            return completions
        }

        return emptyList()
    }
}
