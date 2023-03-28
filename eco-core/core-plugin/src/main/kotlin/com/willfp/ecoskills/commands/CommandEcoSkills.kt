package com.willfp.ecoskills.commands

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.libreforge.loader.LibreforgePlugin
import org.bukkit.command.CommandSender

class CommandEcoSkills(plugin: LibreforgePlugin) :
    PluginCommand(
        plugin,
        "ecoskills",
        "ecoskills.command.ecoskills",
        false
    ) {

    override fun onExecute(sender: CommandSender, args: List<String>) {
        sender.sendMessage(
            plugin.langYml.getMessage("invalid-command")
        )
    }

    init {
        addSubcommand(CommandReload(plugin))
            .addSubcommand(CommandReset(plugin))
            .addSubcommand(CommandGive(plugin))
            .addSubcommand(CommandRecount(plugin))
    }
}
