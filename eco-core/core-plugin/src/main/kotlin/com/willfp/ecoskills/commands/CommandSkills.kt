package com.willfp.ecoskills.commands

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecoskills.gui.menus.SkillsGUI
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.skills.Skills
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

object CommandSkills : PluginCommand(
    plugin,
    "skills",
    "ecoskills.command.skills",
    true
) {
    init {
        this.addSubcommand(CommandToggleActionBar)
            .addSubcommand(CommandToggleXpGainSound)
        if (plugin.configYml.getBool("leaderboard.enabled"))
            this.addSubcommand(CommandTop)
    }

    override fun onExecute(player: Player, args: List<String>) {
        if (args.isEmpty()) {
            SkillsGUI.open(player)
            return
        }

        val skill = notifyNull(Skills.getByID(args.getOrNull(0)), "invalid-skill")
        skill.levelGUI.open(player)
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1) {
            StringUtil.copyPartialMatches(
                args[0],
                Skills.values().map { it.id },
                completions
            )
            return completions
        }

        return emptyList()
    }

    override fun getAliases(): List<String> {
        return listOf(
            "skill"
        )
    }
}
