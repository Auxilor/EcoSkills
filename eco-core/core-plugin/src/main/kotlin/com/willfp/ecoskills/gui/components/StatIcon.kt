package com.willfp.ecoskills.gui.components

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.modify
import com.willfp.eco.util.lineWrap
import com.willfp.ecoskills.api.getStatLevel
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.stats.Stat
import org.bukkit.inventory.ItemStack
import java.util.UUID
import java.util.concurrent.TimeUnit

private val itemBuilderCache = Caffeine.newBuilder()
    .expireAfterWrite(plugin.configYml.getInt("gui.refresh-time").toLong(), TimeUnit.SECONDS)
    .build<MutableMap<UUID, Stat>, ItemStack>()

class StatIcon(
    stat: Stat,
    config: Config,
    plugin: EcoPlugin
) : PositionedComponent {
    private val baseIcon = Items.lookup(config.getString("icon")).item

    private val slot = slot { player, _ ->
        itemBuilderCache.get(mutableMapOf(player.uniqueId to stat)) {
            val level = player.getStatLevel(stat)

            baseIcon.clone().modify {
                setDisplayName(
                    plugin.configYml.getFormattedString("stats-gui.stat-icon.name")
                        .replace("%stat%", stat.name)
                        .let { stat.addPlaceholdersInto(it, level) }
                )

                addLoreLines(
                    stat.addPlaceholdersInto(
                        plugin.configYml.getStrings("stats-gui.stat-icon.lore"),
                        player
                    ).lineWrap(plugin.configYml.getInt("stats-gui.stat-icon.line-wrap"))
                )
            }
        }
    }

    override val isEnabled = config.getBoolOrNull("enabled") ?: true
    override val row = config.getInt("position.row")
    override val column = config.getInt("position.column")

    override fun getSlotAt(row: Int, column: Int) = slot
}
