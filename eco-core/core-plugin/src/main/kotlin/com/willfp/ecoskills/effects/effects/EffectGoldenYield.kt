package com.willfp.ecoskills.effects.effects

import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockDropItemEvent

class EffectGoldenYield: Effect(
    "golden_yield"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%chance%", NumberUtils.format(config.getDouble("chance-per-level") * level))
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: BlockDropItemEvent) {
        if (this.config.getStrings("disabled-in-worlds").contains(event.block.world.name)) {
            return
        }

        val block = event.block
        val player = event.player

        if (!this.checkConditions(player)) {
            return
        }

        val data = block.blockData

        if (data !is Ageable) {
            return
        }

        if (block.type == Material.SUGAR_CANE || block.type == Material.SWEET_BERRY_BUSH || block.type == Material.CACTUS || block.type == Material.BAMBOO) {
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

        if (NumberUtils.randFloat(0.0, 100.0) < chance) {
            for (i in 1..this.config.getInt("drop-multiplier")) {
                DropQueue(player)
                    .addItems(event.items.map { item -> item.itemStack })
                    .push()
            }
        }
    }
}