package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.commands.notifyFalse
import com.willfp.eco.core.commands.notifyPermissionRequired
import com.willfp.eco.core.commands.notifyPlayerRequired
import com.willfp.eco.core.data.profile
import com.willfp.ecoskills.gui.SkillGUI
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.toggleSkillEnabled
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
        if (sender !is Player) {
            sender.sendMessage(this.plugin.langYml.getMessage("not-player"))
            return
        }

        if (args.isEmpty()) {
            SkillGUI.open(sender)
            return
        }

        val id = args[0].lowercase()
        val skill = Skills.getByID(id)

        if (skill == null) {
            sender.sendMessage(this.plugin.langYml.getMessage("invalid-skill"))
            return
        }

        when(args.size) {
            1 -> skill.gui.menu.open(sender)
            2 -> {
                sender.notifyPermissionRequired(togglePerm, "no-permission")
                sender.sendMessage(
                    plugin.langYml.getMessage(when(sender.toggleSkillEnabled(skill)) {
                        true -> "enable-skill"
                        false -> "disable-skill"
                    }).replace("%skill%", skill.name)
                )
            }
            else -> {
                sender.notifyPermissionRequired(togglePerm, "no-permission")
                sender.notifyPermissionRequired(togglePermOthers, "no-permission")
                val player = args[2].notifyPlayerRequired("invalid-player")
                sender.sendMessage(
                    plugin.langYml.getMessage(when(player.toggleSkillEnabled(skill)) {
                        true -> "enable-skill-player"
                        false -> "disable-skill-player"
                    }).replace("%skill%", skill.name).replace("%player%", player.name)
                )
            }
        }
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()

        return when(args.size) {
            0 -> emptyList()
            1 -> {
                StringUtil.copyPartialMatches(
                    args[0],
                    Skills.values().map { it.id },
                    completions
                )
                return completions
            }
            2 -> {
                if(sender.hasPermission(togglePerm)) listOf("toggle") else emptyList()
            }
            else -> {
                if(sender.hasPermission(togglePermOthers)) {
                    StringUtil.copyPartialMatches(
                        args[2],
                        Bukkit.getOnlinePlayers().map { it.name },
                        completions
                    )
                    return completions
                } else emptyList()
            }
        }
    }

    companion object {
        val togglePerm = "ecoskills.command.skills.toggle"
        val togglePermOthers = "${togglePerm}.others"
    }
}
