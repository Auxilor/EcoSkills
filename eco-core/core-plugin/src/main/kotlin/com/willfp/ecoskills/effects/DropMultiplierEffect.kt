package com.willfp.ecoskills.effects

import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class DropMultiplierEffect(
    id: String
) : Effect(id) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%chance%", NumberUtils.format(this.getChance(level)))
            .replace("%multiplier%", this.getMultiplier(level).toString())
    }

    fun handleDropBonus(player: Player, items: Collection<ItemStack>) {
        val level = player.getEffectLevel(this)

        val bonus = generateBonus(getChance(level), getMultiplier(level))

        if (bonus <= 0) {
            return
        }

        DropQueue(player)
            .addItems(items.map { it.apply { amount *= bonus } })
            .push()
    }

    private fun generateBonus(chance: Double, maxMultiplier: Int): Int {
        var bonus = maxMultiplier - 2
        if (NumberUtils.randFloat(0.0, 100.0) <= chance) {
            bonus++
        }

        return bonus
    }

    private fun getMultiplier(level: Int): Int {
        val chance = config.getDouble("chance-per-level") * level

        var add = 2

        if (chance % 100 == 0.0) {
            add = 1
        }

        return (chance / 100).toInt() + add
    }

    fun getChance(level: Int): Double {
        var chance = config.getDouble("chance-per-level") * level

        chance -= (getMultiplier(level) - 2) * 100
        if (chance == 0.0) {
            chance = 100.0
        }

        return chance
    }
}
