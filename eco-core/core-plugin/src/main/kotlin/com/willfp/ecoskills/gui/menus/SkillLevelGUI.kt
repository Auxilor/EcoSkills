package com.willfp.ecoskills.gui.menus

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

            // Instead of the page changer, this will show up when on the first page
            addComponent(
                MenuLayer.LOWER,
                plugin.configYml.getInt("level-gui.progression-slots.prev-page.location.row"),
                plugin.configYml.getInt("level-gui.progression-slots.prev-page.location.column"),
                slot(
                    ItemStackBuilder(Items.lookup(plugin.configYml.getString("level-gui.progression-slots.prev-page.material")))
                        .setDisplayName(plugin.configYml.getString("level-gui.progression-slots.prev-page.name"))
                        .build()
                ) {
                    onLeftClick { player, _, _, _ -> SkillsGUI.open(player) }
                }
            )

            addComponent(
                plugin.configYml.getInt("level-gui.progression-slots.prev-page.location.row"),
                plugin.configYml.getInt("level-gui.progression-slots.prev-page.location.column"),
                PageChanger(
                    ItemStackBuilder(Items.lookup(plugin.configYml.getString("level-gui.progression-slots.prev-page.material")))
                        .setDisplayName(plugin.configYml.getString("level-gui.progression-slots.prev-page.name"))
                        .build(),
                    PageChanger.Direction.BACKWARDS
                )
            )

            addComponent(
                plugin.configYml.getInt("level-gui.progression-slots.next-page.location.row"),
                plugin.configYml.getInt("level-gui.progression-slots.next-page.location.column"),
                PageChanger(
                    ItemStackBuilder(Items.lookup(plugin.configYml.getString("level-gui.progression-slots.next-page.material")))
                        .setDisplayName(plugin.configYml.getString("level-gui.progression-slots.next-page.name"))
                        .build(),
                    PageChanger.Direction.FORWARDS
                )
            )

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
