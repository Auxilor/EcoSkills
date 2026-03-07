package com.willfp.ecoskills.commands

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecoskills.plugin
import org.bukkit.command.CommandSender

object CommandEcoSkills : PluginCommand(
    plugin,
    "ecoskills",
    "ecoskills.command.ecoskills",
    false
) {
    init {
        this.addSubcommand(CommandReload)
            .addSubcommand(CommandGive)
            .addSubcommand(CommandReset)
            .addSubcommand(CommandRecount)
            .addSubcommand(CommandSet)
    }

    override fun onExecute(sender: CommandSender, args: List<String>) {
        sender.sendMessage(
            plugin.langYml.getMessage("invalid-command")
        )
    }
}
