package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.CommandHandler
import com.willfp.eco.core.command.impl.PluginCommand
import org.bukkit.command.CommandSender

class CommandEcoskills(plugin: EcoPlugin) :
    PluginCommand(
        plugin,
        "ecoskills",
        "ecoskills.command.ecoskills",
        false
    ) {
    override fun getHandler(): CommandHandler {
        return CommandHandler { sender: CommandSender, _: List<String> ->
            sender.sendMessage(
                plugin.langYml.getMessage("invalid-command")
            )
        }
    }

    init {
        addSubcommand(CommandReload(plugin))
    }
}