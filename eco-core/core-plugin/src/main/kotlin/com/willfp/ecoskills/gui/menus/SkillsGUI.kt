package com.willfp.ecoskills.gui.menus

import com.willfp.eco.core.gui.menu
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot.ConfigSlot
import com.willfp.eco.core.gui.slot.FillerMask
import com.willfp.eco.core.gui.slot.MaskItems
import com.willfp.ecoskills.gui.components.CloseButton
import com.willfp.ecoskills.gui.components.PlayerInfoIcon
import com.willfp.ecoskills.gui.components.addComponent
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.skills.Skills
import org.bukkit.entity.Player

object SkillsGUI {
    private lateinit var menu: Menu

    internal fun update() {
        menu = buildMenu()
    }

    private fun buildMenu(): Menu {
        return menu(plugin.configYml.getInt("gui.rows")) {
            title = plugin.langYml.getFormattedString("menu.title")

            setMask(
                FillerMask(
                    MaskItems.fromItemNames(plugin.configYml.getStrings("gui.mask.materials")),
                    *plugin.configYml.getStrings("gui.mask.pattern").toTypedArray()
                )
            )

            addComponent(
                PlayerInfoIcon(
                    plugin.configYml.getSubsection("gui.player-info"),
                    opensStatMenu = true
                )
            )

            for (skill in Skills.values()) {
                addComponent(skill.icon)
            }

            addComponent(
                CloseButton(
                    plugin.configYml.getSubsection("gui.close")
                )
            )

            for (config in plugin.configYml.getSubsections("gui.custom-slots")) {
                setSlot(
                    config.getInt("row"),
                    config.getInt("column"),
                    ConfigSlot(config)
                )
            }
        }
    }

    fun open(player: Player) {
        menu.open(player)
    }
}
