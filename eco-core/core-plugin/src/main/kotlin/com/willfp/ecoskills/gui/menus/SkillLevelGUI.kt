package com.willfp.ecoskills.gui.menus

import com.willfp.eco.core.gui.addPageChanger
import com.willfp.eco.core.gui.menu
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.menu.MenuLayer
import com.willfp.eco.core.gui.onLeftClick
import com.willfp.eco.core.gui.page.PageChanger
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.gui.slot.ConfigSlot
import com.willfp.eco.core.gui.slot.FillerMask
import com.willfp.eco.core.gui.slot.MaskItems
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.ItemStackBuilder
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
        val maskPattern = plugin.configYml.getStrings("level-gui.mask.pattern").toTypedArray()
        val maskItems = MaskItems.fromItemNames(plugin.configYml.getStrings("level-gui.mask.materials"))

        val levelComponent = SkillLevelComponent(skill)

        val pageChangeSound = PlayableSound.create(plugin.configYml.getSubsection("level-gui.page-change-sound"))

        menu = menu(plugin.configYml.getInt("level-gui.rows")) {
            title = plugin.configYml.getString("level-gui.title")
                .replace("%skill%", skill.name)
                .formatEco()

            maxPages(levelComponent.pages)

            setMask(
                FillerMask(
                    maskItems,
                    *maskPattern
                )
            )

            addComponent(1, 1, levelComponent)

            defaultPage {
                levelComponent.getPageOf(it.getSkillLevel(skill)).coerceAtLeast(1)
            }

            val prevPath = "level-gui.progression-slots.prev-page"
            val nextPath = "level-gui.progression-slots.next-page"

            addComponent(
                MenuLayer.LOWER,
                plugin.configYml.getInt("$prevPath.location.row"),
                plugin.configYml.getInt("$prevPath.location.column"),
                slot(
                    plugin.configYml.getStringOrNull("$prevPath.item")
                        ?.let { Items.lookup(it).item }
                        ?: ItemStackBuilder(Items.lookup("arrow")).build()
                ) {
                    onLeftClick { player, _, _, _ -> SkillsGUI.open(player) }
                }
            )

            addPageChanger(plugin.configYml, prevPath, PageChanger.Direction.BACKWARDS, pageChangeSound)
            addPageChanger(plugin.configYml, nextPath, PageChanger.Direction.FORWARDS, pageChangeSound)

            val closeEnabled = plugin.configYml.getBoolOrNull("level-gui.progression-slots.close.enabled") ?: true
            if (closeEnabled) {
                setSlot(
                    plugin.configYml.getInt("level-gui.progression-slots.close.location.row"),
                    plugin.configYml.getInt("level-gui.progression-slots.close.location.column"),
                    slot(
                        ItemStackBuilder(Items.lookup(plugin.configYml.getString("level-gui.progression-slots.close.material")))
                            .setDisplayName(plugin.configYml.getString("level-gui.progression-slots.close.name"))
                            .build()
                    ) {
                        onLeftClick { event, _ ->
                            event.whoClicked.closeInventory()
                        }
                    }
                )
            }

            for (config in plugin.configYml.getSubsections("level-gui.custom-slots")) {
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
