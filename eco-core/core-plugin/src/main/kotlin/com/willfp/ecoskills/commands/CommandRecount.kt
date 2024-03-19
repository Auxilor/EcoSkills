package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.ecoskills.api.getSkillLevel
import com.willfp.ecoskills.api.getSkillProgress
import com.willfp.ecoskills.api.giveSkillXP
import com.willfp.ecoskills.api.setSkillLevel
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.effects.effects
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stats
import com.willfp.ecoskills.stats.stats
import com.willfp.ecoskills.util.offlinePlayers
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class CommandRecount(plugin: EcoPlugin) :
    Subcommand(
        plugin,
        "recount",
        "ecoskills.command.recount",
        false
    ) {

    override fun onExecute(sender: CommandSender, args: List<String>) {
        val players = offlinePlayers(args, 0, "invalid-player")

        if (players.size > 1) {
            sender.sendMessage(plugin.langYml.getMessage("recounting-all-players"))
        }

        for (player in players) {
            for (stat in Stats.values()) {
                player.stats.reset(stat)
            }
            for (effect in Effects.values()) {
                player.effects.reset(effect)
            }
            for (skill in Skills.values()) {
                val level = player.getSkillLevel(skill)
                if (level > 0) {
                    for (i in (0 until level)) {
                        skill.giveRewards(player, i)
                    }
                }
            }
        }

        if (players.size > 1) {
            sender.sendMessage(plugin.langYml.getMessage("recounted-all-players"))
            return
        } else {
            sender.sendMessage(plugin.langYml.getMessage("recounted-player"))
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
