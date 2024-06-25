package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import org.bukkit.command.CommandSender

class CommandEcoSkills(plugin: EcoPlugin) : PluginCommand(
    plugin,
    "ecoskills",
    "ecoskills.command.ecoskills",
    false
) {
    init {
        this.addSubcommand(CommandReload(plugin))
            .addSubcommand(CommandGive(plugin))
            .addSubcommand(CommandReset(plugin))
            .addSubcommand(CommandRecount(plugin))
            .addSubcommand(CommandSet(plugin))
    }

    override fun onExecute(sender: CommandSender, args: List<String>) {
        sender.sendMessage(
            plugin.langYml.getMessage("invalid-command")
        )
    }
}
