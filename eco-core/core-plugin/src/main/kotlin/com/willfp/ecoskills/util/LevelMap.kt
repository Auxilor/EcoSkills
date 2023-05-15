package com.willfp.ecoskills.util

import com.willfp.ecoskills.Levellable
import org.bukkit.OfflinePlayer

class LevelMap<T : Levellable>(
    private val player: OfflinePlayer
) {
    operator fun get(levellable: T): Int {
        return levellable.getSavedLevel(player)
    }

    operator fun set(levellable: T, level: Int) {
        require(level >= 0) { "Level must be positive" }

        levellable.setSavedLevel(player, level)
    }

    fun reset(levellable: T) {
        levellable.setSavedLevel(player, levellable.startLevel)
    }
}
