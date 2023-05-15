package com.willfp.ecoskills.mana

import com.willfp.eco.core.map.nestedMap
import org.bukkit.entity.Player
import java.util.UUID


private val map = nestedMap<UUID, MagicType, Int>()

class MagicMap(
    private val player: Player
) {
    operator fun get(type: MagicType): Int {
        return map[player.uniqueId][type] ?: 0
    }

    operator fun set(type: MagicType, amount: Int) {
        val actualAmount = amount.coerceIn(0..type.getLimit(player))

        map[player.uniqueId][type] = actualAmount
    }
}
