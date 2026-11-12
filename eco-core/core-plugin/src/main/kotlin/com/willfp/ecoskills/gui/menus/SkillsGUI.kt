package com.willfp.ecoskills.gui.menus

import com.willfp.ecoskills.gui.components.PlayerInfoIcon
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.skills.Skills
import org.bukkit.entity.Player
import wiki.ecohub.modules.menus.addCustomSlots
import wiki.ecohub.modules.menus.component.addComponent
import wiki.ecohub.modules.menus.component.closeButton
import wiki.ecohub.modules.menus.menu
import wiki.ecohub.modules.menus.menu.Menu
import wiki.ecohub.modules.menus.setMask

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

            addComponent(closeButton(plugin.configYml.getSubsection("gui.close")))

            addCustomSlots(plugin.configYml, "gui")
        }
    }

    fun open(player: Player) {
        menu.open(player)
    }
}
