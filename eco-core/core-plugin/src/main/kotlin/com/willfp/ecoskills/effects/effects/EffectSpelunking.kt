package com.willfp.ecoskills.effects.effects

import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.data.isPlayerPlaced
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent

class EffectSpelunking : Effect(
    "spelunking"
) {
    private val blockMap = mutableMapOf<Location, Material>()
    private val noRepeat = mutableListOf<BlockDropItemEvent>()

    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%chance%", NumberUtils.format(this.getChance(level)))
            .replace("%multiplier%", this.getMultiplier(level).toString())
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBreak(event: BlockBreakEvent) {
        blockMap[event.block.location] = event.block.type

        this.plugin.scheduler.runLater({
            blockMap.remove(event.block.location)
        }, 1)
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun handle(event: BlockDropItemEvent) {
        if (noRepeat.contains(event)) {
            return
        }

        val mat = blockMap[event.block.location] ?: return

        val player = event.player

        val block = event.block

        if (block.isPlayerPlaced()) {
            return
        }

        if (!config.getStrings("on-blocks").contains(mat.name.lowercase())) {
            return
        }

        val level = player.getEffectLevel(this)

        val chance = getChance(level)

        val multiplier = getMultiplier(level)

        if (multiplier == 1) {
            return
        }

        val dropEvent = BlockDropItemEvent(block, block.state, player, event.items)
        Bukkit.getPluginManager().callEvent(dropEvent)

        if (dropEvent.items.isEmpty()) {
            return
        }

        if (multiplier > 2) {
            for (i in 2 until multiplier) {
                DropQueue(player)
                    .addItems(*dropEvent.items.map { item -> item.itemStack })
                    .push()
            }
        }

        if (NumberUtils.randFloat(0.0, 100.0) < chance) {
            DropQueue(player)
                .addItems(*dropEvent.items.map { item -> item.itemStack })
                .push()
        }
    }

    private fun getMultiplier(level: Int): Int {
        val chance = config.getDouble("chance-per-level") * level

        var add = 2

        if (chance % 100 == 0.0) {
            add = 1
        }

        return (chance / 100).toInt() + add
    }

    private fun getChance(level: Int): Double {
        var chance = config.getDouble("chance-per-level") * level

        chance -= ((getMultiplier(level) - 2) * 100)
        if (chance == 0.0) {
            chance = 100.0
        }

        return chance
    }
}