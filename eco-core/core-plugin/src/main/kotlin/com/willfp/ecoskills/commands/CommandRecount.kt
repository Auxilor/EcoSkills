package com.willfp.ecoskills.commands

import com.willfp.ecoskills.api.getSkillLevel
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.effects.effects
import com.willfp.ecoskills.runOwnedIfOnline
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stats
import com.willfp.ecoskills.stats.stats
import com.willfp.ecoskills.util.knownPlayer
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import wiki.ecohub.core.command.CommandBuilder
import wiki.ecohub.core.command.CommandContext
import wiki.ecohub.data.playerbase.forEachSavedProfile
import java.util.logging.Level

internal fun CommandBuilder<CommandSender>.recountCommand() = subcommand(
    name = "recount",
    permission = "ecoskills.command.recount"
) {
    executes {
        if (args.firstOrNull().equals("all", ignoreCase = true)) {
            recountAllPlayers()
        } else {
            val player = argument(0, "invalid-player", "invalid-player") { knownPlayer(it) }

            player.runOwnedIfOnline {
                recount(player)

                reply("recounted-player")
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

private fun CommandContext<CommandSender>.recountAllPlayers() {
    reply("recounting-all-players")

    forEachSavedProfile(plugin) { uuid ->
        val player = Bukkit.getOfflinePlayer(uuid)

        player.runOwnedIfOnline { recount(player) }
    }.thenRun {
        plugin.scheduler.global().run { reply("recounted-all-players") }
    }.exceptionally { error ->
        plugin.logger.log(Level.SEVERE, "Failed to recount every player", error)
        null
    }
}

private fun recount(player: OfflinePlayer) {
    Stats.values().forEach { player.stats.reset(it) }
    Effects.values().forEach { player.effects.reset(it) }
    Skills.values().forEach { skill ->
        for (i in 1..player.getSkillLevel(skill)) {
            skill.giveRewards(player, i)
        }
    }
}
