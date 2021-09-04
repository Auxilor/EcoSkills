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
        false
    ) {

    init {
        this.addSubcommand(CommandTop(plugin))
            .addSubcommand(CommandRank(plugin))
    }

    override fun getHandler(): CommandHandler {
        return CommandHandler { sender: CommandSender, _: List<String> ->
            if (sender !is Player) {
                sender.sendMessage(this.plugin.langYml.getMessage("not-player"))
                return@CommandHandler
            }

            SkillGUI.getHomeMenu().open(sender)
        }
    }
}