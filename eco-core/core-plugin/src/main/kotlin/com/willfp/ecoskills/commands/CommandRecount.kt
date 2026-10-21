package com.willfp.ecoskills.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.core.data.forEachSavedProfile
import com.willfp.ecoskills.api.getSkillLevel
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.effects.effects
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.runOwnedIfOnline
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stats
import com.willfp.ecoskills.stats.stats
import com.willfp.ecoskills.util.offlinePlayer
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

object CommandRecount : Subcommand(
    plugin,
    "recount",
    "ecoskills.command.recount",
    false
) {

    override fun onExecute(sender: CommandSender, args: List<String>) {
        if (args.firstOrNull().equals("all", ignoreCase = true)) {
            recountAllPlayers(sender)
            return
        }

        val player = notifyNull(offlinePlayer(args, 0, "invalid-player"), "invalid-player")

        player.runOwnedIfOnline {
            recount(player)

            sender.sendMessage(plugin.langYml.getMessage("recounted-player"))
        }
    }

    private fun recountAllPlayers(sender: CommandSender) {
        sender.sendMessage(plugin.langYml.getMessage("recounting-all-players"))

        forEachSavedProfile(plugin) { uuid ->
            val player = Bukkit.getOfflinePlayer(uuid)

            player.runOwnedIfOnline { recount(player) }
        }.thenRun {
            plugin.scheduler.global().run { sender.sendMessage(plugin.langYml.getMessage("recounted-all-players")) }
        }
    }

    private fun recount(player: OfflinePlayer) {
        for (stat in Stats.values()) {
            player.stats.reset(stat)
        }
        for (effect in Effects.values()) {
            player.effects.reset(effect)
        }
        for (skill in Skills.values()) {
            val level = player.getSkillLevel(skill)
            for (i in 1..level) {
                skill.giveRewards(player, i)
            }
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
