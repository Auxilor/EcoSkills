package com.willfp.ecoskills.gui.menus

import com.willfp.ecoskills.api.getSkillLevel
import com.willfp.ecoskills.gui.components.SkillLevelComponent
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.skills.Skill
import org.bukkit.entity.Player
import wiki.ecohub.game.sound.PlayableSound
import wiki.ecohub.modules.menus.addCustomSlots
import wiki.ecohub.modules.menus.addPageChanger
import wiki.ecohub.modules.menus.component.ConfigButton
import wiki.ecohub.modules.menus.component.addComponent
import wiki.ecohub.modules.menus.component.closeButton
import wiki.ecohub.modules.menus.menu
import wiki.ecohub.modules.menus.menu.Menu
import wiki.ecohub.modules.menus.menu.MenuLayer
import wiki.ecohub.modules.menus.page.PageChanger
import wiki.ecohub.modules.menus.setMask
import wiki.ecohub.ui.text.formatEco

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
