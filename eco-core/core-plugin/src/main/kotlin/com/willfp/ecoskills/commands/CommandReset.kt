@file:Suppress("DEPRECATION")

package com.willfp.ecoskills.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.ecoskills.api.resetSkills
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.util.offlinePlayers
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

object CommandReset : Subcommand(
    plugin,
    "reset",
    "ecoskills.command.reset",
    false
) {

    override fun onExecute(sender: CommandSender, args: List<String>) {
        val players = offlinePlayers(args, 0, "invalid-player")

        if (players.size > 1) {
            sender.sendMessage(plugin.langYml.getMessage("resetting-all-players"))
        }

        for (player in players) {
            player.resetSkills()
        }

        if (players.size > 1) {
            sender.sendMessage(plugin.langYml.getMessage("reset-all-players"))
            return
        } else {
            sender.sendMessage(plugin.langYml.getMessage("reset-player"))
        }
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1) {
            StringUtil.copyPartialMatches(
                args[0],
                listOf("all") union Bukkit.getOnlinePlayers().map { player -> player.name },
                completions
            )
            return completions
        }

        return emptyList()
    }
}
