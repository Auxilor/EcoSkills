package com.willfp.ecoskills.commands

import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.gui.menus.StatsGUI
import wiki.ecohub.core.command.playerCommand

internal fun EcoSkillsPlugin.statsCommand() = playerCommand(name = "stats", permission = "ecoskills.command.stats") {
    aliases("attributes", "stat")

    executes {
        StatsGUI.open(sender)
    }
}
