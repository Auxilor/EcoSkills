package com.willfp.ecoskills.gui

import com.willfp.eco.core.config.updating.ConfigUpdater
import com.willfp.eco.core.fast.fast
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
import com.willfp.eco.util.formatEco
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.getStatLevel
import com.willfp.ecoskills.stats.Stats
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.SkullMeta

object StatsGUI {
    @JvmStatic
    private lateinit var menu: Menu

    @JvmStatic
    @ConfigUpdater
    fun update(plugin: EcoSkillsPlugin) {
        menu = buildStatsMenu(plugin)
    }

    @JvmStatic
    private fun buildStatsMenu(plugin: EcoSkillsPlugin): Menu {
        val maskPattern = plugin.configYml.getStrings("stats-gui.mask.pattern").toTypedArray()
        val backItem = Items.lookup(plugin.configYml.getString("stats-gui.back.material")).item
        val playerHeadItemBuilder = { player: Player, _: Menu ->
            val itemStack = SkullBuilder()
                .setDisplayName(
                    @Suppress("DEPRECATION")
                    StringUtils.format(
                        plugin.configYml.getString("stats-gui.player-info.name")
                            .replace("%player%", player.displayName), player
                    )
                )
                .addLoreLines {
                    val lore: MutableList<String> = ArrayList()
                    for (string in plugin.configYml.getStrings("stats-gui.player-info.lore")) {
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

        return menu(plugin.configYml.getInt("stats-gui.rows")) {
            setTitle(plugin.langYml.getString("stats-gui.title"))
            setMask(
                FillerMask(
                    MaskItems.fromItemNames(plugin.configYml.getStrings("stats-gui.mask.materials")),
                    *maskPattern
                )
            )
            setSlot(
                plugin.configYml.getInt("stats-gui.player-info.row"),
                plugin.configYml.getInt("stats-gui.player-info.column"),
                slot(playerHeadItemBuilder) {}
            )
            modfiy { menuBuilder ->
                for (stat in Stats.values()) {
                    if (!stat.config.getBool("stats-gui.enabled")) {
                        continue
                    }

                    menuBuilder.setSlot(
                        stat.config.getInt("stats-gui.position.row"),
                        stat.config.getInt("stats-gui.position.column"),
                        slot(
                            Items.lookup(stat.config.getString("stats-gui.item.item")).item
                        ) {
                            setUpdater { player, _, item ->
                                item.clone().fast().apply {
                                    displayName = stat.config.getString("stats-gui.item.name")
                                        .replace("%stat%", stat.name)
                                        .replace("%level%", player.getStatLevel(stat).toString())
                                        .replace("%description%", stat.getDescription(player.getStatLevel(stat)))
                                        .formatEco(player, true)
                                    lore = stat.config.getStrings("stats-gui.item.lore")
                                        .map { it.replace("%stat%", stat.name) }
                                        .map { it.replace("%level%", player.getStatLevel(stat).toString()) }
                                        .map {
                                            it.replace(
                                                "%description%",
                                                stat.getDescription(player.getStatLevel(stat))
                                            )
                                        }
                                        .formatEco(player, true)
                                }.unwrap()
                            }
                        }
                    )
                }
            }

            setSlot(plugin.configYml.getInt("stats-gui.back.location.row"),
                plugin.configYml.getInt("stats-gui.back.location.column"),
                slot(
                    ItemStackBuilder(backItem)
                        .setDisplayName(plugin.configYml.getString("stats-gui.back.name"))
                        .build()
                ) {
                    onLeftClick { event, _ -> SkillGUI.open(event.whoClicked as Player) }
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
