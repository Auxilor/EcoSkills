package com.willfp.ecoskills.gui.components

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.gui.onLeftClick
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.items.builder.SkullBuilder
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.savedDisplayName
import com.willfp.ecoskills.gui.menus.StatsGUI
import com.willfp.ecoskills.plugin
import org.bukkit.inventory.meta.SkullMeta
import java.util.UUID
import java.util.concurrent.TimeUnit

private val skullBuilderCache = Caffeine.newBuilder()
    .expireAfterWrite(plugin.configYml.getInt("gui.refresh-time").toLong(), TimeUnit.SECONDS)
    .build<UUID, SkullBuilder>()

class PlayerInfoIcon(
    config: Config,
    opensStatMenu: Boolean
) : PositionedComponent {
    private val slot = slot({ player, _ ->
        val skullBuilder = skullBuilderCache.get(player.uniqueId) {
            SkullBuilder()
                .setDisplayName(
                    config.getString("name")
                        .replace("%player%", player.savedDisplayName)
                        .formatEco(player, true)
                )
                .addLoreLines(
                    config.getFormattedStrings(
                        "lore",
                        placeholderContext(
                            player = player
                        )
                    )
                )
                .apply {
                    if (opensStatMenu) {
                        addLoreLines(
                            config.getFormattedStrings(
                                "view-more",
                                placeholderContext(
                                    player = player
                                )
                            )
                        )
                    }
                }
        }

        val skull = skullBuilder.build()

        val meta = skull.itemMeta as SkullMeta
        meta.owningPlayer = player
        skull.itemMeta = meta
        skull
    }) {
        if (opensStatMenu) {
            onLeftClick { player, _, _, _ ->
                StatsGUI.open(player)
            }
        }
    }

    override val row: Int = config.getInt("row")
    override val column: Int = config.getInt("column")

    override fun getSlotAt(row: Int, column: Int) = slot
}
