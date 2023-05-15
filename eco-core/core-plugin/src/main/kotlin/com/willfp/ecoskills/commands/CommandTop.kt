package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.savedDisplayName
import com.willfp.ecoskills.skills.Skills
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil


class CommandTop(plugin: EcoPlugin) :
    Subcommand(
        plugin,
        "top",
        "ecoskills.command.top",
        false
    ) {

    override fun onExecute(sender: CommandSender, args: List<String>) {
        val skill = Skills.getByID(args.getOrNull(0))

        val pageIndex = if (skill == null) 0 else 1
        val page = args.getOrNull(pageIndex)?.toIntOrNull() ?: 1

        if (
            skill == null
            && args.getOrNull(pageIndex)?.toIntOrNull() == null
            && args.getOrNull(pageIndex)?.isBlank() == false
        ) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-skill"))
            return
        }

        val offset = (page - 1) * 10

        val positions = ((offset + page)..(offset + page + 9)).toList()

        val top = if (skill == null) {
            positions.mapNotNull { Skills.getTop(it) }
        } else {
            positions.mapNotNull { skill.getTop(it) }
        }

        val messages = plugin.langYml.getStrings("top.format")
        val lines = mutableListOf<String>()

        for ((index, entry) in top.withIndex()) {
            val (player, level) = entry

            val line = plugin.langYml.getString("top-line-format")
                .replace("%rank%", positions[index].toString())
                .replace("%level%", level.toString())
                .replace("%player%", player.savedDisplayName)

            lines.add(line)
        }

        val linesIndex = messages.indexOf("%lines%")

        if (linesIndex != -1) {
            messages.removeAt(linesIndex)
            messages.addAll(linesIndex, lines)
        }

        for (message in messages) {
            sender.sendMessage(
                message.formatEco(
                    placeholderContext(
                        player = sender as? Player
                    )
                )
            )
        }
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1) {
            StringUtil.copyPartialMatches(
                args[0],
                listOf(1, 2, 3, 4, 5).map { it.toString() },
                completions
            )
            return completions
        }

        return emptyList()
    }
}
