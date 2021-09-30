package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.CommandHandler
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.ecoskills.data.storage.PlayerProfile
import org.bukkit.command.CommandSender

class CommandReload(plugin: EcoPlugin) :
    Subcommand(
        plugin,
        "reload",
        "ecoskills.command.reload",
        false
    ) {
    override fun getHandler(): CommandHandler {
        return CommandHandler { sender: CommandSender, _: List<String> ->
            PlayerProfile.saveAll(false)
            plugin.reload()
            sender.sendMessage(plugin.langYml.getMessage("reloaded"))
        }
    }
}