package com.willfp.ecoskills.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.runGlobal
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stats
import org.bukkit.command.CommandSender
import wiki.ecohub.core.stdlib.toNiceString
import wiki.ecohub.ui.text.StringUtils

object CommandReload : Subcommand(
    plugin,
    "reload",
    "ecoskills.command.reload",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        runGlobal {
            sender.sendMessage(
                plugin.langYml.getMessage("reloaded", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                    .replace("%time%", plugin.reloadWithTime().toNiceString())
                    .replace("%stats%", Stats.values().size.toNiceString())
                    .replace("%skills%", Skills.values().size.toNiceString())
            )
        }
    }
}
