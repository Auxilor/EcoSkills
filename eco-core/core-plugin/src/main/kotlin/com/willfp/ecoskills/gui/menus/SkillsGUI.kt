package com.willfp.ecoskills.gui.menus

import com.willfp.eco.core.gui.addCustomSlots
import com.willfp.eco.core.gui.component.addComponent
import com.willfp.eco.core.gui.menu
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.setMask
import com.willfp.ecoskills.gui.components.CloseButton
import com.willfp.ecoskills.gui.components.PlayerInfoIcon
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.skills.Skills
import org.bukkit.entity.Player

object SkillsGUI {
    @Volatile
    private lateinit var menu: Menu

    internal fun update() {
        menu = buildMenu()
    }

    private fun buildMenu(): Menu {
        return menu(plugin.configYml.getInt("gui.rows")) {
            title = plugin.langYml.getFormattedString("menu.title")

            setMask(plugin.configYml, "gui")

            addComponent(
                PlayerInfoIcon(
                    plugin.configYml.getSubsection("gui.player-info"),
                    opensStatMenu = true
                )
            )

            for (skill in Skills.values()) {
                addComponent(skill.icon)
            }

            val closeEnabled = plugin.configYml.getBoolOrNull("gui.close.enabled") ?: true
            if (closeEnabled) {
                addComponent(
                    CloseButton(
                        plugin.configYml.getSubsection("gui.close")
                    )
                )
            }

            addCustomSlots(plugin.configYml, "gui")
        }
    }

    fun open(player: Player) {
        menu.open(player)
    }
}
