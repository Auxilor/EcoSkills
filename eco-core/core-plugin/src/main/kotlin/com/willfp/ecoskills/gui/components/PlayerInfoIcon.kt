package com.willfp.ecoskills.gui.components

import com.willfp.ecoskills.gui.menus.StatsGUI
import com.willfp.ecoskills.plugin
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import wiki.ecohub.config.Config
import wiki.ecohub.data.cache.EcoCache
import wiki.ecohub.game.entity.player.PlayerUtils
import wiki.ecohub.game.item.builder.SkullBuilder
import wiki.ecohub.modules.menus.component.PositionedComponent
import wiki.ecohub.modules.menus.getSlotLocations
import wiki.ecohub.modules.menus.onLeftClick
import wiki.ecohub.modules.menus.slot
import wiki.ecohub.ui.placeholder.placeholderContext
import wiki.ecohub.ui.text.formatEco
import java.time.Duration
import java.util.UUID

private data class PlayerInfoKey(val player: UUID, val opensStatMenu: Boolean)

private val skullCache = EcoCache.builder<PlayerInfoKey, ItemStack>()
    .expireAfterWrite(Duration.ofMillis(plugin.configYml.getInt("gui.cache-ttl").toLong()))
    .build()

class PlayerInfoIcon(
    config: Config,
    opensStatMenu: Boolean
) : PositionedComponent {
    override val isEnabled = config.getBoolOrNull("enabled") ?: true
    private val slot = if (isEnabled) {
        slot({ player, _ ->
            skullCache.get(PlayerInfoKey(player.uniqueId, opensStatMenu)) {
                val skullBuilder = SkullBuilder()
                    .setDisplayName(
                        config.getString("name")
                            .replace("%player%", PlayerUtils.getSavedDisplayName(player))
                            .formatEco(player, true)
                    )
                    .addLoreLines(
                        config.getFormattedStrings(
                            "lore", // Ensure correct path for lore
                            placeholderContext(player = player)
                        )
                    )
                    .apply {
                        if (opensStatMenu) {
                            addLoreLines(
                                config.getFormattedStrings(
                                    "view-more",
                                    placeholderContext(player = player)
                                )
                            )
                        }
                    }

                skullBuilder.build().apply {
                    val meta = itemMeta as SkullMeta
                    meta.owningPlayer = player
                    itemMeta = meta
                }
            }
        }) {
            if (opensStatMenu) {
                onLeftClick { player, _, _, _ ->
                    StatsGUI.open(player)
                }
            }
        }
    } else {
        null
    }

    override val locations = config.getSlotLocations()

    override fun getSlotAt(row: Int, column: Int) = slot
}
