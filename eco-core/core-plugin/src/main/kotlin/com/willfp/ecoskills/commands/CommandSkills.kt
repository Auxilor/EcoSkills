package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecoskills.gui.SkillGUI
import com.willfp.ecoskills.skills.Skills
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class CommandSkills(plugin: EcoPlugin) :
    PluginCommand(
        plugin,
        "skills",
        "ecoskills.command.skills",
        false
    ) {

    init {
        this.addSubcommand(CommandTop(plugin))
            .addSubcommand(CommandRank(plugin))
            .addSubcommand(CommandToggleActionbar(plugin))
            .addSubcommand(CommandToggleSound(plugin))
    }

    override fun onExecute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) {
            sender.sendMessage(this.plugin.langYml.getMessage("not-player"))
            return
        }

        if (args.isEmpty()) {
            SkillGUI.open(sender)
        }

        val id = args[0].lowercase()
        val skill = Skills.getByID(id)

        if (skill == null) {
            sender.sendMessage(this.plugin.langYml.getMessage("invalid-skill"))
            return
        }

        skill.gui.menu.open(sender)
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
}
