package com.willfp.ecoskills.gui.menus

import com.willfp.eco.core.gui.menu
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot.ConfigSlot
import com.willfp.eco.core.gui.slot.FillerMask
import com.willfp.eco.core.gui.slot.MaskItems
import com.willfp.ecoskills.gui.components.BackButton
import com.willfp.ecoskills.gui.components.CloseButton
import com.willfp.ecoskills.gui.components.PlayerInfoIcon
import com.willfp.ecoskills.gui.components.addComponent
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.stats.Stats
import org.bukkit.entity.Player

object StatsGUI {
    private lateinit var menu: Menu

    internal fun update() {
        menu = buildMenu()
    }

    private fun buildMenu(): Menu {
        return menu(plugin.configYml.getInt("stats-gui.rows")) {
            title = plugin.langYml.getString("stats-gui.title")

            setMask(
                FillerMask(
                    MaskItems.fromItemNames(plugin.configYml.getStrings("stats-gui.mask.materials")),
                    *plugin.configYml.getStrings("stats-gui.mask.pattern").toTypedArray()
                )
            )

            addComponent(
                PlayerInfoIcon(
                    plugin.configYml.getSubsection("gui.player-info"),
                    opensStatMenu = false
                )
            )

            for (stat in Stats.values()) {
                addComponent(stat.icon)
            }

            addComponent(
                CloseButton(
                    plugin.configYml.getSubsection("stats-gui.close")
                )
            )

            addComponent(
                BackButton(
                    plugin.configYml.getSubsection("stats-gui.back")
                ) {
                    SkillsGUI.open(it)
                }
            )

            for (config in plugin.configYml.getSubsections("stats-gui.custom-slots")) {
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
