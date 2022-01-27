@file:Suppress("DEPRECATION")

package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.ecoskills.resetSkills
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class CommandReset(plugin: EcoPlugin) :
    Subcommand(
        plugin,
        "reset",
        "ecoskills.command.reset",
        false
    ) {

    override fun onExecute(sender: CommandSender, args: List<String>) {
        if (args.isEmpty()) {
            sender.sendMessage(plugin.langYml.getMessage("requires-player"))
            return
        }

        if (args[0].equals("all", ignoreCase = true)) {
            sender.sendMessage(plugin.langYml.getMessage("resetting-all-players"))
            Bukkit.getOfflinePlayers().forEach { it.resetSkills() }
            sender.sendMessage(plugin.langYml.getMessage("reset-all-players"))
        } else {
            val player = Bukkit.getOfflinePlayer(args[0])
            if (!player.hasPlayedBefore()) {
                sender.sendMessage(plugin.langYml.getMessage("invalid-player"))
                return
            }

            sender.sendMessage(plugin.langYml.getMessage("reset-player"))
            player.resetSkills()
        }
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1) {
            StringUtil.copyPartialMatches(
                args[0],
                Bukkit.getOnlinePlayers().map { player -> player.name }.toMutableList(),
                completions
            )
            return completions
        }

        return emptyList()
    }
}