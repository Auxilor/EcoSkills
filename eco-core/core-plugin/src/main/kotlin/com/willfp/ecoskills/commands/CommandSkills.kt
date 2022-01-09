package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecoskills.gui.SkillGUI
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

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

        SkillGUI.homeMenu.open(sender)
    }
}