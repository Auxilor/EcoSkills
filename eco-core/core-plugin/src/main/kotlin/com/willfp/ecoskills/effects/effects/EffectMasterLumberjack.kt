package com.willfp.ecoskills.effects.effects

import com.willfp.eco.util.BlockUtils
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.ecoskills.effects.DropMultiplierEffect
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent

class EffectMasterLumberjack : DropMultiplierEffect(
    "master_lumberjack"
) {
    private val blockMap = mutableMapOf<Location, Material>()

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

        val mat = blockMap[event.block.location] ?: return

        val player = event.player

        if (!this.checkConditions(player)) {
            return
        }

        val block = event.block

        if (BlockUtils.isPlayerPlaced(block)) {
            return
        }

        if (!config.getStrings("on-blocks").containsIgnoreCase(mat.name)) {
            return
        }

        this.handleDropBonus(player, event.items.map { it.itemStack.clone() })
    }
}
