package com.willfp.ecoskills.gui.menus

import com.willfp.eco.core.gui.addCustomSlots
import com.willfp.eco.core.gui.component.ConfigButton
import com.willfp.eco.core.gui.component.addComponent
import com.willfp.eco.core.gui.component.closeButton
import com.willfp.eco.core.gui.menu
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.setMask
import com.willfp.ecoskills.gui.components.PlayerInfoIcon
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.stats.Stats
import org.bukkit.entity.Player

object StatsGUI {
    @Volatile
    private lateinit var menu: Menu

    internal fun update() {
        menu = buildMenu()
    }

    private fun buildMenu(): Menu {
        return menu(plugin.configYml.getInt("stats-gui.rows")) {
            title = plugin.langYml.getString("stats-gui.title")

            setMask(plugin.configYml, "stats-gui")

            addComponent(
                PlayerInfoIcon(
                    plugin.configYml.getSubsection("gui.player-info"),
                    opensStatMenu = false
                )
            )

            for (stat in Stats.values()) {
                addComponent(stat.icon)
            }

            addComponent(closeButton(plugin.configYml.getSubsection("stats-gui.close")))

            addComponent(
                ConfigButton(plugin.configYml.getSubsection("stats-gui.back")) { player, _, _, _ ->
                    SkillsGUI.open(player)
                }
            )

            addCustomSlots(plugin.configYml, "stats-gui")
        }
    }

    fun open(player: Player) {
        menu.open(player)
    }
}
