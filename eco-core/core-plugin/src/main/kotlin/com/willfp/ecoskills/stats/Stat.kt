package com.willfp.ecoskills.stats

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.util.toNiceString
import com.willfp.eco.util.toNumeral
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.api.getBaseStatLevel
import com.willfp.ecoskills.api.getBonusStatLevel
import com.willfp.ecoskills.api.getStatLevel
import com.willfp.ecoskills.obj.LevellableWithHolder
import com.willfp.ecoskills.util.LevelMap
import org.bukkit.OfflinePlayer

class Stat(
    id: String,
    config: Config,
    plugin: EcoSkillsPlugin
) : LevellableWithHolder(id, config, plugin) {
    init {
        PlayerPlaceholder(plugin, "${id}_base") {
            it.getBaseStatLevel(this).toNiceString()
        }.register()

        PlayerPlaceholder(plugin, "${id}_bonus") {
            it.getBonusStatLevel(this).toNiceString()
        }.register()
    }

    override fun getActualLevel(player: OfflinePlayer): Int {
        return player.getStatLevel(this)
    }
}

internal val OfflinePlayer.stats: LevelMap<Stat>
    get() = LevelMap(this)
