package com.willfp.ecoskills.gui.components

import com.willfp.ecoskills.api.getStatLevel
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.stats.Stat
import org.bukkit.inventory.ItemStack
import wiki.ecohub.config.Config
import wiki.ecohub.data.cache.EcoCache
import wiki.ecohub.game.item.Items
import wiki.ecohub.game.item.modify
import wiki.ecohub.modules.menus.component.PositionedComponent
import wiki.ecohub.modules.menus.getSlotLocations
import wiki.ecohub.modules.menus.slot
import wiki.ecohub.ui.text.lineWrap
import java.time.Duration
import java.util.UUID

private data class StatIconKey(val player: UUID, val stat: String)

private val itemCache = EcoCache.builder<StatIconKey, ItemStack>()
    .expireAfterWrite(Duration.ofMillis(plugin.configYml.getInt("gui.cache-ttl").toLong()))
    .build()

class StatIcon(
    stat: Stat,
    config: Config
) : PositionedComponent {
    private val baseIcon = Items.lookup(config.getString("icon")).getItem()

    private val slot = slot { player, _ ->
        itemCache.get(StatIconKey(player.uniqueId, stat.id)) {
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
    override val locations = config.getSlotLocations("position")

    override fun getSlotAt(row: Int, column: Int) = slot
}
