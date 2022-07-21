package com.willfp.ecoskills.effects.effects

import com.willfp.ecoskills.effects.DropMultiplierEffect
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent

class EffectBountifulHarvest : DropMultiplierEffect(
    "bountiful_harvest"
) {
    private val blockMap = mutableMapOf<Location, Material>()

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBreak(event: BlockBreakEvent) {
        blockMap[event.block.location] = event.block.type

        this.plugin.scheduler.runLater(5) {
            blockMap.remove(event.block.location)
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun handle(event: BlockDropItemEvent) {
        if (this.config.getStrings("disabled-in-worlds").contains(event.block.world.name)) {
            return
        }

        val mat = blockMap[event.block.location] ?: return

        val state = (event.blockState.blockData as Ageable)

        if (state.age != state.maximumAge) {
            return
        }

        if (mat == Material.SUGAR_CANE || mat == Material.SWEET_BERRY_BUSH || mat == Material.CACTUS || mat == Material.BAMBOO) {
            return
        }

        if (!config.getStrings("on-blocks").contains(mat.name.lowercase())) {
            return
        }

        this.handleDropBonus(event.player, event.items.map { it.itemStack.clone() })
    }
}