package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.CommandHandler
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.ecoskills.EcoSkillsPlugin
import org.bukkit.command.CommandSender
import java.io.IOException

class CommandReload(plugin: EcoPlugin) :
    Subcommand(
        plugin,
        "reload",
        "ecoskills.command.reload",
        false
    ) {
    override fun getHandler(): CommandHandler {
        return CommandHandler { sender: CommandSender, _: List<String> ->
            try {
                (plugin as EcoSkillsPlugin).dataHandler.save()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            plugin.reload()
            sender.sendMessage(plugin.langYml.getMessage("reloaded"))
        }
    }
}