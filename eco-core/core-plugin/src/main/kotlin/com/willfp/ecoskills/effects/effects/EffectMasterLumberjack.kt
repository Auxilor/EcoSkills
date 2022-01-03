package com.willfp.ecoskills.effects.effects

import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.util.BlockUtils
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent

class EffectMasterLumberjack : Effect(
    "master_lumberjack"
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

        this.plugin.scheduler.run {
            blockMap.remove(event.block.location)
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun handle(event: BlockDropItemEvent) {
        if (this.config.getStrings("disabled-in-worlds").contains(event.block.world.name)) {
            return
        }

        if (noRepeat.contains(event)) {
            return
        }

        val mat = blockMap[event.block.location] ?: return

        val player = event.player

        val block = event.block

        if (BlockUtils.isPlayerPlaced(block)) {
            return
        }

        if (!config.getStrings("on-blocks", false).contains(mat.name.lowercase())) {
            return
        }

        val level = player.getEffectLevel(this)

        val chance = getChance(level)

        var bonus = getMultiplier(level) - 2

        if (bonus <= 0) {
            return
        }

        if (NumberUtils.randFloat(0.0, 100.0) < chance) {
            bonus++
        }

        val dropEvent = BlockDropItemEvent(block, block.state, player, event.items.map {
            it.itemStack = it.itemStack.apply {
                this.amount *= bonus
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