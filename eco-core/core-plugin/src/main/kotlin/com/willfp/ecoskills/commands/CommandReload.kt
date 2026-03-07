package com.willfp.ecoskills.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.ecoskills.plugin
import org.bukkit.command.CommandSender

object CommandReload : Subcommand(
    plugin,
    "reload",
    "ecoskills.command.reload",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        plugin.reload()
        sender.sendMessage(plugin.langYml.getMessage("reloaded"))
    }
}
