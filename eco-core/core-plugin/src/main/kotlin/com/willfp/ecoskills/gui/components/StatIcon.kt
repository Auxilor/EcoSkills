package com.willfp.ecoskills.gui.components

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.modify
import com.willfp.eco.util.toNumeral
import com.willfp.ecoskills.api.getSkillLevel
import com.willfp.ecoskills.api.getStatLevel
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.stats.Stat

class StatIcon(
    stat: Stat,
    config: Config,
    plugin: EcoPlugin
) : PositionedComponent {
    private val baseIcon = Items.lookup(config.getString("icon")).item

    private val slot = slot { player, _ ->
        val level = player.getStatLevel(stat)

        baseIcon.clone().modify {
            setDisplayName(
                plugin.configYml.getFormattedString("stats-gui.stat-icon.name")
                    .replace("%level%", level.toString())
                    .replace("%level_numeral%", level.toNumeral())
                    .replace("%stat%", stat.name)
            )

            addLoreLines(
                stat.addPlaceholdersInto(
                    plugin.configYml.getStrings("stats-gui.stat-icon.lore"),
                    player
                )
            )
        }
    }

    override val isEnabled = config.getBool("enabled")
    override val row = config.getInt("position.row")
    override val column = config.getInt("position.column")

    override fun getSlotAt(row: Int, column: Int) = slot
}
