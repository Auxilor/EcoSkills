package com.willfp.ecoskills.gui

import com.willfp.eco.core.config.updating.ConfigUpdater
import com.willfp.eco.core.gui.menu
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.gui.slot.ConfigSlot
import com.willfp.eco.core.gui.slot.FillerMask
import com.willfp.eco.core.gui.slot.MaskItems
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.ItemStackBuilder
import com.willfp.eco.core.items.builder.SkullBuilder
import com.willfp.eco.util.StringUtils
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.skills.Skills
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.SkullMeta

object SkillGUI {
    @JvmStatic
    private lateinit var menu: Menu

    @JvmStatic
    @ConfigUpdater
    fun update(plugin: EcoSkillsPlugin) {
        menu = buildHomeMenu(plugin)
    }

    @JvmStatic
    private fun buildHomeMenu(plugin: EcoSkillsPlugin): Menu {
        val maskPattern = plugin.configYml.getStrings("gui.mask.pattern").toTypedArray()
        val closeItem = Items.lookup(plugin.configYml.getString("gui.close.material")).item
        val playerHeadItemBuilder = { player: Player, _: Menu ->
            val itemStack = SkullBuilder()
                .setDisplayName(
                    @Suppress("DEPRECATION")
                    StringUtils.format(
                        plugin.configYml.getString("gui.player-info.name")
                            .replace("%player%", player.displayName), player
                    )
                )
                .addLoreLines {
                    val lore: MutableList<String> = ArrayList()
                    for (string in plugin.configYml.getStrings("gui.player-info.lore")) {
                        lore.add(StringUtils.format(string!!, player))
                    }
                    lore
                }
                .build()
            val meta = itemStack.itemMeta as SkullMeta
            meta.owningPlayer = player
            itemStack.itemMeta = meta
            itemStack
        }
        return menu(plugin.configYml.getInt("gui.rows")) {
            setTitle(plugin.langYml.getString("menu.title"))
            setMask(
                FillerMask(
                    MaskItems.fromItemNames(plugin.configYml.getStrings("gui.mask.materials")),
                    *maskPattern
                )
            )
            setSlot(
                plugin.configYml.getInt("gui.player-info.row"),
                plugin.configYml.getInt("gui.player-info.column"),
                slot(playerHeadItemBuilder) {
                    if (plugin.configYml.getBool("gui.player-info.click-to-open-stats")) {
                        onLeftClick { event, _ ->
                            val player = event.whoClicked as Player
                            StatsGUI.open(player)
                        }
                    }
                }
            )

            for (skill in Skills.values()) {
                if (skill.enabled) {
                    setSlot(
                        skill.config.getInt("gui.position.row"),
                        skill.config.getInt("gui.position.column"),
                        skill.gui.slot
                    )
                }
            }

            setSlot(plugin.configYml.getInt("gui.close.location.row"),
                plugin.configYml.getInt("gui.close.location.column"),
                slot(
                    ItemStackBuilder(closeItem)
                        .setDisplayName(plugin.configYml.getString("gui.close.name"))
                        .build()
                ) {
                    onLeftClick { event, _ -> event.whoClicked.closeInventory() }
                }
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
