package com.willfp.ecoskills.magic

import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.context.PlaceholderContextSupplier
import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.PriceFactory
import org.bukkit.entity.Player
import java.util.UUID

class PriceFactoryMagic(
    private val type: MagicType
) : PriceFactory {
    override fun getNames() = listOf(type.id)

    override fun create(baseContext: PlaceholderContext, function: PlaceholderContextSupplier<Double>): Price {
        return PriceMagic(baseContext) { function.get(it) }
    }

    private inner class PriceMagic(
        private val baseContext: PlaceholderContext,
        private val function: (PlaceholderContext) -> Double
    ) : Price {
        private val multipliers = mutableMapOf<UUID, Double>()

        override fun canAfford(player: Player, multiplier: Double): Boolean {
            return player.magic[type] >= getValue(player, multiplier)
        }

        override fun pay(player: Player, multiplier: Double) {
            player.magic[type] -= getValue(player, multiplier).toInt()
        }

        override fun giveTo(player: Player, multiplier: Double) {
            player.magic[type] += getValue(player, multiplier).toInt()
        }

        override fun getValue(player: Player, multiplier: Double): Double {
            return function(baseContext.copyWithPlayer(player)) * getMultiplier(player) * multiplier
        }

        override fun getMultiplier(player: Player): Double {
            return multipliers[player.uniqueId] ?: 1.0
        }

        override fun setMultiplier(player: Player, multiplier: Double) {
            multipliers[player.uniqueId] = multiplier
        }

        override fun getIdentifier(): String {
            return "ecoskills:${type.id}"
        }
    }
}
