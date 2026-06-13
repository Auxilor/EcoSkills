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
import org.bukkit.inventory.ItemStack

class SkillLevelGUI(
    private val skill: Skill
) {
    private val menu: Menu

    init {
        val maskPattern = plugin.configYml.getStrings("level-gui.mask.pattern").toTypedArray()
        val maskItems = MaskItems.fromItemNames(plugin.configYml.getStrings("level-gui.mask.materials"))

        val levelComponent = SkillLevelComponent(skill)

        val pageChangeSound = PlayableSound.create(plugin.configYml.getSubsection("level-gui.page-change-sound"))

        fun pageButtonItem(basePath: String, state: String): ItemStack? {
            val itemString = plugin.configYml.getStringOrNull(if (state == "active") "$basePath.item" else "$basePath.item-inactive")
                ?: plugin.configYml.getStringOrNull(if (state == "active") "$basePath.material" else "$basePath.material-inactive")
                ?: return null

            val builder = ItemStackBuilder(Items.lookup(itemString))

            // Deprecated: use the item/item-inactive keys to set the name instead
            val name = plugin.configYml.getStringOrNull(if (state == "active") "$basePath.name" else "$basePath.name-inactive")
            if (name != null) {
                builder.setDisplayName(name)
            }

            return builder.build()
        }

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

            val prevRow = plugin.configYml.getInt("$prevPath.location.row")
            val prevColumn = plugin.configYml.getInt("$prevPath.location.column")
            val nextRow = plugin.configYml.getInt("$nextPath.location.row")
            val nextColumn = plugin.configYml.getInt("$nextPath.location.column")

            addComponent(
                MenuLayer.LOWER,
                prevRow,
                prevColumn,
                slot(
                    pageButtonItem(prevPath, "active")
                        ?: ItemStackBuilder(Items.lookup("arrow")).build()
                ) {
                    onLeftClick { player, _, _, _ -> SkillsGUI.open(player) }
                }
            )

            pageButtonItem(prevPath, "active")?.let { active ->
                addPageChanger(PageChanger.Direction.BACKWARDS, active, null, pageChangeSound, prevRow, prevColumn)
            }

            pageButtonItem(nextPath, "active")?.let { active ->
                addPageChanger(PageChanger.Direction.FORWARDS, active, pageButtonItem(nextPath, "inactive"), pageChangeSound, nextRow, nextColumn)
            }

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
