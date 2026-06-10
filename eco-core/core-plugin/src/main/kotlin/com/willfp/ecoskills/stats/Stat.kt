package com.willfp.ecoskills.stats

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.LevellableWithHolder
import com.willfp.ecoskills.Placeholders
import com.willfp.ecoskills.api.getStatLevel
import com.willfp.ecoskills.gui.components.StatIcon
import com.willfp.ecoskills.util.LevelMap
import org.bukkit.OfflinePlayer

class Stat(
    id: String,
    config: Config
) : LevellableWithHolder(id, config) {
    val icon = StatIcon(this, config.getSubsection("gui"))

    init {
        Placeholders.applyExternalStatPlaceholders(this)
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
