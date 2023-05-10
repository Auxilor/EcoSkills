package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import org.bukkit.entity.Player

class CommandSkills(plugin: EcoPlugin) : PluginCommand(
    plugin,
    "skills",
    "ecoskills.command.skills",
    true
) {
    override fun onExecute(player: Player, args: List<String>) {

    }
}
