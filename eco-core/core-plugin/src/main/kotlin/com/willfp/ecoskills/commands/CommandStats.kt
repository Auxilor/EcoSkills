package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecoskills.gui.menus.StatsGUI
import org.bukkit.entity.Player

class CommandStats(plugin: EcoPlugin) : PluginCommand(
    plugin,
    "stats",
    "ecoskills.command.stats",
    true
) {

    override fun onExecute(player: Player, args: List<String>) {
        StatsGUI.open(player)
        return
    }

    override fun getAliases(): List<String> {
        return listOf(
            "attributes",
            "stat"
        )
    }
}
