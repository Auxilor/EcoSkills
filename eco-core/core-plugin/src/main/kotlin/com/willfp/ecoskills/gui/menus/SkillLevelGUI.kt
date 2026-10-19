package com.willfp.ecoskills.gui.menus

import com.willfp.eco.core.gui.addCustomSlots
import com.willfp.eco.core.gui.addPageChanger
import com.willfp.eco.core.gui.component.ConfigButton
import com.willfp.eco.core.gui.component.addComponent
import com.willfp.eco.core.gui.component.closeButton
import com.willfp.eco.core.gui.menu
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.menu.MenuLayer
import com.willfp.eco.core.gui.page.PageChanger
import com.willfp.eco.core.gui.setMask
import com.willfp.eco.core.sound.PlayableSound
import com.willfp.eco.util.formatEco
import com.willfp.ecoskills.api.getSkillLevel
import com.willfp.ecoskills.gui.components.SkillLevelComponent
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.skills.Skill
import org.bukkit.entity.Player

class SkillLevelGUI(
    private val skill: Skill
) {
    private val menu: Menu

    init {
        val levelComponent = SkillLevelComponent(skill)

        val pageChangeSound = PlayableSound.create(plugin.configYml.getSubsection("level-gui.page-change-sound"))

        menu = menu(plugin.configYml.getInt("level-gui.rows")) {
            title = plugin.configYml.getString("level-gui.title")
                .replace("%skill%", skill.name)
                .formatEco()

            maxPages(levelComponent.pages)

            setMask(plugin.configYml, "level-gui")

            addComponent(1, 1, levelComponent)

            defaultPage {
                levelComponent.getPageOf(it.getSkillLevel(skill)).coerceAtLeast(1)
            }

            val prevPath = "level-gui.progression-slots.prev-page"
            val nextPath = "level-gui.progression-slots.next-page"

            addComponent(
                ConfigButton(
                    plugin.configYml.getSubsection(prevPath),
                    defaultItem = "arrow",
                    layer = MenuLayer.LOWER
                ) { player, _, _, _ -> SkillsGUI.open(player) }
            )

            addPageChanger(plugin.configYml, prevPath, PageChanger.Direction.BACKWARDS, pageChangeSound)
            addPageChanger(plugin.configYml, nextPath, PageChanger.Direction.FORWARDS, pageChangeSound)

            addComponent(closeButton(plugin.configYml.getSubsection("level-gui.progression-slots.close")))

            addCustomSlots(plugin.configYml, "level-gui")
        }
    }

    fun open(player: Player) {
        menu.open(player)
    }
}
