package com.willfp.ecoskills.magic

import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap


private val map = ConcurrentHashMap<UUID, MutableMap<MagicType, Int>>()

class MagicMap(
    private val player: Player
) {
    private val values: MutableMap<MagicType, Int>
        get() = map.computeIfAbsent(player.uniqueId) { ConcurrentHashMap() }

    operator fun get(type: MagicType): Int {
        return values[type] ?: 0
    }

    operator fun set(type: MagicType, amount: Int) {
        val actualAmount = amount.coerceIn(0..type.getLimit(player))

        values[type] = actualAmount
    }
}
