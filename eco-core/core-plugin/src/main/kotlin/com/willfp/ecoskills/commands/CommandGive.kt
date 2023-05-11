package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.util.StringUtils
import com.willfp.eco.util.formatEco
import com.willfp.ecoskills.api.giveSkillXP
import com.willfp.ecoskills.skills.Skills
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
        if (args.isEmpty()) {
            sender.sendMessage(plugin.langYml.getMessage("requires-player"))
            return
        }

        if (args.size == 1) {
            sender.sendMessage(plugin.langYml.getMessage("requires-skill"))
            return
        }

        if (args.size == 2) {
            sender.sendMessage(plugin.langYml.getMessage("requires-amount"))
            return
        }

        val player = Bukkit.getPlayer(args[0])

        if (player == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-player"))
            return
        }

        val obj = Skills.getByID(args[1].lowercase())

        if (obj == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-skill"))
            return
        }

        val amount = args[2].toIntOrNull()

        if (amount == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-amount"))
            return
        }

        player.giveSkillXP(obj, amount.toDouble())

        sender.sendMessage(
            this.plugin.langYml.getMessage("gave-skill-xp", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                .replace("%player%", player.name)
                .replace("%amount%", amount.toString())
                .replace("%skill%", obj.name)
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
                Skills.values().map { it.id },
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
