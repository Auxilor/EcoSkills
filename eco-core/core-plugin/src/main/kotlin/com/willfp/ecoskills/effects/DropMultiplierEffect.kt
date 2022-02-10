package com.willfp.ecoskills.effects

import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.Bukkit
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockDropItemEvent

abstract class DropMultiplierEffect(
    id: String
) : Effect(id) {
    private val noRepeat = mutableListOf<BlockDropItemEvent>()

    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%chance%", NumberUtils.format(this.getChance(level)))
            .replace("%multiplier%", this.getMultiplier(level).toString())
    }

    fun handleDropBonus(event: BlockDropItemEvent, player: Player, block: Block) {
        if (noRepeat.contains(event)) {
            return
        }

        val level = player.getEffectLevel(this)

        val bonus = generateBonus(getChance(level), getMultiplier(level))

        if (bonus <= 1) {
            return
        }

        val dropEvent = BlockDropItemEvent(block, block.state, player, event.items.map {
            it.itemStack = it.itemStack.apply {
                amount *= bonus
            }
            it
        })

        noRepeat.add(dropEvent)

        Bukkit.getPluginManager().callEvent(dropEvent)

        if (dropEvent.items.isEmpty() || dropEvent.isCancelled) {
            return
        }

        DropQueue(player)
            .addItems(dropEvent.items.map { it.itemStack })
            .push()
    }

    private fun generateBonus(chance: Double, maxMultiplier: Int): Int {
        var bonus = maxMultiplier - 1
        if (NumberUtils.randFloat(0.0, 100.0) < chance) {
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
