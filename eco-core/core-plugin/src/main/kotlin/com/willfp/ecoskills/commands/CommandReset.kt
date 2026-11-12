package com.willfp.ecoskills.commands

import com.willfp.ecoskills.api.resetSkills
import com.willfp.ecoskills.runOwnedIfOnline
import com.willfp.ecoskills.util.knownPlayer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import wiki.ecohub.core.command.CommandBuilder
import wiki.ecohub.core.command.CommandContext
import wiki.ecohub.data.playerbase.forEachSavedProfile
import java.util.logging.Level

internal fun CommandBuilder<CommandSender>.resetCommand() = subcommand(
    name = "reset",
    permission = "ecoskills.command.reset"
) {
    executes {
        if (args.firstOrNull().equals("all", ignoreCase = true)) {
            resetAllPlayers()
        } else {
            val player = argument(0, "invalid-player", "invalid-player") { knownPlayer(it) }

            player.runOwnedIfOnline {
                @Suppress("DEPRECATION")
                player.resetSkills()

                reply("reset-player")
            }
        }
    }

    completes {
        if (args.size == 1) {
            listOf("all") + playerNames()
        } else {
            emptyList()
        }
    }
}

private fun CommandContext<CommandSender>.resetAllPlayers() {
    reply("resetting-all-players")

    forEachSavedProfile(plugin) { uuid ->
        val player = Bukkit.getOfflinePlayer(uuid)

        @Suppress("DEPRECATION")
        player.runOwnedIfOnline { player.resetSkills() }
    }.thenRun {
        plugin.scheduler.global().run { reply("reset-all-players") }
    }.exceptionally { error ->
        plugin.logger.log(Level.SEVERE, "Failed to reset skills for every player", error)
        null
    }
}
