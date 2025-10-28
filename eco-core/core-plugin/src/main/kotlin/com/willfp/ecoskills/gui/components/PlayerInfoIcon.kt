package com.willfp.ecoskills.gui.components

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.gui.onLeftClick
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.modify
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.savedDisplayName
import com.willfp.ecoskills.gui.menus.StatsGUI
import com.willfp.ecoskills.plugin
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.concurrent.TimeUnit

private val iconCache = Caffeine.newBuilder()
    .expireAfterWrite(plugin.configYml.getInt("gui.cache-ttl").toLong(), TimeUnit.MILLISECONDS)
    .build<Int, ItemStack>()

class PlayerInfoIcon(
    config: Config,
    opensStatMenu: Boolean
) : PositionedComponent {
    private val baseIcon = Items.lookup(config.getString("icon")).item
        get() = field.clone()

    override val isEnabled = config.getBoolOrNull("enabled") ?: true
    private val slot = if (isEnabled) {
        slot({ player, _ ->
            iconCache.get(player.uniqueId.hashCode()) {
                baseIcon.modify {
                    setDisplayName(
                        config.getFormattedString("name")
                            .replace("%player%", player.savedDisplayName)
                            .formatEco(player, true)
                    )
                    .addLoreLines(
                        config.getFormattedStrings(
                            "lore",
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

                val meta = baseIcon.itemMeta
                if (meta is SkullMeta && meta.owningPlayer == null) {
                    meta.owningPlayer = player
                    baseIcon.itemMeta = meta
                }
                    baseIcon
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

    override val row: Int = config.getInt("row")
    override val column: Int = config.getInt("column")

    override fun getSlotAt(row: Int, column: Int) = slot
}
