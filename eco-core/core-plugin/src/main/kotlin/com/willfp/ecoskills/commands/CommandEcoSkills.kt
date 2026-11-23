package com.willfp.ecoskills.commands

import com.willfp.ecoskills.EcoSkillsPlugin
import wiki.ecohub.core.command.command

internal fun EcoSkillsPlugin.ecoSkillsCommand() = command(
    name = "ecoskills",
    permission = "ecoskills.command.ecoskills"
) {
    reloadCommand()
    giveCommand()
    resetCommand()
    recountCommand()
    setCommand()
}
