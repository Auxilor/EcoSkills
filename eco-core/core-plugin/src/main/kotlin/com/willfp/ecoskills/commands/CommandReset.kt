@file:Suppress("DEPRECATION")

package com.willfp.ecoskills.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.core.data.forEachSavedProfile
import com.willfp.ecoskills.api.resetSkills
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.runOwnedIfOnline
import com.willfp.ecoskills.util.offlinePlayer
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
        if (args.firstOrNull().equals("all", ignoreCase = true)) {
            resetAllPlayers(sender)
            return
        }

        val player = notifyNull(offlinePlayer(args, 0, "invalid-player"), "invalid-player")

        player.runOwnedIfOnline {
            player.resetSkills()

            sender.sendMessage(plugin.langYml.getMessage("reset-player"))
        }
    }

    private fun resetAllPlayers(sender: CommandSender) {
        sender.sendMessage(plugin.langYml.getMessage("resetting-all-players"))

        forEachSavedProfile(plugin) { uuid ->
            val player = Bukkit.getOfflinePlayer(uuid)

            player.runOwnedIfOnline { player.resetSkills() }
        }.thenRun {
            plugin.scheduler.global().run { sender.sendMessage(plugin.langYml.getMessage("reset-all-players")) }
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
