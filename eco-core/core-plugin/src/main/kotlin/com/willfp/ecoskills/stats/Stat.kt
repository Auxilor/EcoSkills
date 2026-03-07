package com.willfp.ecoskills.stats

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.toNiceString
import com.willfp.ecoskills.LevellableWithHolder
import com.willfp.ecoskills.api.getBaseStatLevel
import com.willfp.ecoskills.api.getBonusStatLevel
import com.willfp.ecoskills.api.getStatLevel
import com.willfp.ecoskills.gui.components.StatIcon
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.util.LevelMap
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class Stat(
    id: String,
    config: Config
) : LevellableWithHolder(id, config) {
    val icon = StatIcon(this, config.getSubsection("gui"))

    /**
     * Add stat placeholders into [strings], to be shown to a [player].
     */
    fun addPlaceholdersInto(
        strings: List<String>,
        player: Player,
        level: Int = player.getStatLevel(this)
    ): List<String> {
        // Replace placeholders in the strings with their actual values.
        return strings.map { s ->
            s.replace("%description%", this.getDescription(level))
                .replace("%stat%", this.name)
                .let { addPlaceholdersInto(it, level) }
        }.formatEco(player)
    }

    init {
        PlayerPlaceholder(plugin, "${id}_base") {
            it.getBaseStatLevel(this).toNiceString()
        }.register()

        PlayerPlaceholder(plugin, "${id}_bonus") {
            val bonus = it.getBonusStatLevel(this)
            when {
                bonus > 0 -> "+${bonus}"
                bonus < 0 -> "$bonus"
                else -> ""
            }
        }.register()
    }

    override fun getActualLevel(player: OfflinePlayer): Int {
        return player.getStatLevel(this)
    }

    override fun equals(other: Any?): Boolean {
        return other is Stat && other.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

internal val OfflinePlayer.stats: LevelMap<Stat>
    get() = LevelMap(this)
