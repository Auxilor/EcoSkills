package com.willfp.ecoskills.commands

import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.gui.menus.SkillsGUI
import com.willfp.ecoskills.skills.Skills
import org.bukkit.entity.Player
import wiki.ecohub.core.command.command

/**
 * Any sender can run `top`; the GUI and the toggles need a player.
 */
internal fun EcoSkillsPlugin.skillsCommand() = command(name = "skills", permission = "ecoskills.command.skills") {
    aliases("skill")

    toggleActionBarCommand()
    toggleXpGainSoundCommand()
    topCommand()

    executes {
        val player = sender as? Player ?: fail("not-player")
        val skill = args.firstOrNull()?.let { Skills.getByID(it) ?: fail("invalid-skill") }

        if (skill == null) {
            SkillsGUI.open(player)
        } else {
            skill.levelGUI.open(player)
        }
    }

    completes {
        if (args.size == 1) {
            Skills.values().map { it.id }
        } else {
            emptyList()
        }
    }
}
