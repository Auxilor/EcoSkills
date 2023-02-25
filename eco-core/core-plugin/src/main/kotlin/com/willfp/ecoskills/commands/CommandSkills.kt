package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecoskills.gui.SkillGUI
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stat
import com.willfp.ecoskills.stats.Stats
import com.willfp.ecoskills.toggleStatEnabled
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class CommandSkills(plugin: EcoPlugin) :
    PluginCommand(
        plugin,
        "skills",
        "ecoskills.command.skills",
        false
    ) {

    init {
        this.addSubcommand(CommandTop(plugin))
            .addSubcommand(CommandRank(plugin))
            .addSubcommand(CommandToggleActionbar(plugin))
            .addSubcommand(CommandToggleSound(plugin))
    }

    override fun onExecute(sender: CommandSender, args: List<String>) {
        if(args.size < 2) {
            if(sender is Player) {
                SkillGUI.open(sender)
            } else {
                sender.sendMessage(this.plugin.langYml.getMessage("not-player"))
            }
            return
        }

        val type = args[0].lowercase()
        val id = args[1].lowercase()
        val result : Pair<Skill?, Stat?> = when (type) {
            "skill" -> Pair(Skills.getByID(id), null)
            "stat" -> Pair(null, Stats.getByID(id))
            else -> null
        }.let {
            if(it == null || it.first == null && it.second == null) {
                notify("requires-skill-or-stat")
                return@let Pair(null, null)
            }

            return@let it
        }


        if(result.first != null) { // Skill
            sender.sendMessage("${plugin.langYml.prefix}&cThis feature is not finished.")
            return
        } else { //Stat
            if(sender is Player) {
                notifyPermissionRequired(sender, togglePerm, "no-permission");
                val stat = result.second!!
                sender.sendMessage(plugin.langYml.getMessage(when(sender.toggleStatEnabled(stat)) {
                    true -> "enable-stat"
                    false -> "disable-stat"
                }).replace("%stat%", stat.name))
                stat.updateStatLevel(sender)
            }
        }
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()


        fun String.copyPartialMatches(values: List<String>, completions: MutableList<String>) {
            StringUtil.copyPartialMatches(this, values, completions);
        }

        return when(args.size) {
            0 -> emptyList()
            1 -> listOf("stat")
            2 -> {
               if(sender.hasPermission(togglePerm)) {
                   if (args[0].contains("stat", true)) {
                       args[1].copyPartialMatches(Stats.values().map { it.id }, completions)
                   }

                   if (args[0].contains("skills", true)) {
                       args[1].copyPartialMatches(Skills.values().map { it.id }, completions)
                   }
               }

                return completions
            }
            3 -> {
                if(sender.hasPermission(togglePerm)) {
                    listOf("toggle")
                } else emptyList()
            }
            else -> {
                if(sender.hasPermission(togglePermOthers)) {
                    args[2].copyPartialMatches(Bukkit.getOnlinePlayers().map { it.name }, completions)
                    completions
                } else emptyList()
            }
        }
    }

    companion object {
        val togglePerm = "ecoskills.command.toggle"
        val togglePermOthers = "${togglePerm}.others"
    }
}
