package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.CommandHandler
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecoskills.gui.SkillGUI
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSkills(plugin: EcoPlugin) :
    PluginCommand(
        plugin,
        "skills",
        "ecoskills.command.skills",
        true
    ) {

    init {
        this.addSubcommand(CommandTop(plugin))
    }

    override fun getHandler(): CommandHandler {
        return CommandHandler { sender: CommandSender, _: List<String> ->
            val player = sender as Player
            SkillGUI.getHomeMenu().open(player)
        }
    }
}