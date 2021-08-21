package com.willfp.ecoskills.effects.effects

import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import com.willfp.ecoskills.getStatLevel
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent

class EffectBountifulHarvest: Effect(
    "bountiful_harvest"
) {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: BlockDropItemEvent) {
        val block = event.block
        val player = event.player

        val data = block.blockData

        if (data !is Ageable) {
            return
        }

        if (block.type == Material.SUGAR_CANE || block.type == Material.SWEET_BERRY_BUSH || block.type == Material.CACTUS) {
            return
        }

        if (data.age != data.maximumAge) {
            return
        }

        if (event.items.isEmpty()) {
            return
        }

        val level = player.getEffectLevel(this)

        val chance = config.getDouble("chance-per-level") * level

        val base = (chance / 100).toInt()

        if (base >= 1) {
            for (i in 1..base) {
                DropQueue(player)
                    .addItems(*event.items.map { item -> item.itemStack })
                    .push()
            }
        }

        if (NumberUtils.randFloat(0.0, 100.0) < chance - (base * 100)) {
            DropQueue(player)
                .addItems(*event.items.map { item -> item.itemStack })
                .push()
        }
    }
}