package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.CommandHandler
import com.willfp.eco.core.command.TabCompleteHandler
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.util.PlayerUtils
import com.willfp.eco.util.StringUtils
import com.willfp.ecoskills.data.LeaderboardHandler
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.getSkillLevel
import com.willfp.ecoskills.setEffectLevel
import com.willfp.ecoskills.skills.Skills
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class CommandRecount(plugin: EcoPlugin): Subcommand(
    plugin,
    "recount",
    "ecoskills.command.recount",
    false
) {

    override fun getHandler(): CommandHandler {
        return CommandHandler { sender: CommandSender, args: List<String> ->

            if (args.isEmpty()) {
                sender.sendMessage(plugin.langYml.getMessage("requires-player"))
                return@CommandHandler
            }

            val player = Bukkit.getPlayer(args[0])

            if (player == null) {
                sender.sendMessage(plugin.langYml.getMessage("invalid-player"))
                return@CommandHandler
            }

            if (args.size < 2) {
                sender.sendMessage(plugin.langYml.getMessage("requires-effect"))
                return@CommandHandler
            }

            val effect = Effects.getByID(args[1].lowercase())

            if (effect == null) {
                if (args[1].lowercase().contentEquals("all")) {
                    var total = 0;
                    for (ceffect in Effects.values()) {
                        total+=recount(player, ceffect)
                    }
                    sender.sendMessage(
                        plugin.langYml.getMessage("recounted-player")
                            .replace("%player%", player.name)
                            .replace("%effect%", "&6ALL")
                            .replace("%level%", total.toString())
                    )
                } else {
                    sender.sendMessage(plugin.langYml.getMessage("invalid-effect"))
                    return@CommandHandler
                }
            } else {
                sender.sendMessage(
                    plugin.langYml.getMessage("recounted-player")
                        .replace("%player%", player.name)
                        .replace("%effect%", effect.id)
                        .replace("%level%", recount(player, effect).toString())
                )
            }

        }
    }

    override fun getTabCompleter(): TabCompleteHandler {
        return TabCompleteHandler { _, args ->
            val completions = mutableListOf<String>()

            if (args.size == 1) {
                StringUtil.copyPartialMatches(
                    args[0],
                    Bukkit.getOnlinePlayers().map { player -> player.name }.toMutableList(),
                    completions
                )
                return@TabCompleteHandler completions
            }

            if (args.size == 2) {
                completions.add("all")
                StringUtil.copyPartialMatches(
                    args[1],
                    Effects.values().map { effect -> effect.id }.toMutableList(),
                    completions
                )
                return@TabCompleteHandler completions
            }

            return@TabCompleteHandler emptyList<String>()
        }
    }

    private fun recount(player: Player, effect: Effect): Int {
        var total = 0
        for (skill in Skills.values()) {

            var ofSkill = 0
            val range = 1..player.getSkillLevel(skill)

            for (reward in skill.getLevelUpRewards()) {
                if (reward.obj is Effect && reward.obj == effect) {
                    for (i in range) {
                        val obj = reward.obj
                        val toGive = skill.getLevelUpReward(obj, i)
                        ofSkill+=toGive
                    }
                }
            }
            total+=ofSkill
        }
        player.setEffectLevel(effect, total)
        return total
    }

}