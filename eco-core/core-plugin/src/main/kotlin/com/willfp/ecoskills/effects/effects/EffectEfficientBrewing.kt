package com.willfp.ecoskills.effects.effects

import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.Bukkit
import org.bukkit.block.BrewingStand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.InventoryClickEvent


class EffectEfficientBrewing : Effect(
    "efficient_brewing"
) {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: InventoryClickEvent) {
        val player = event.whoClicked

        if (player !is Player) {
            return
        }

        if (player.getEffectLevel(this) == 0) {
            return
        }

        if (player.openInventory.topInventory.holder !is BrewingStand) {
            return
        }

        val ticksLess = player.getEffectLevel(this) * this.config.getInt("ticks-less-per-level")

        this.plugin.scheduler.runLater({
            val stand = player.openInventory.topInventory.holder
            if (stand !is BrewingStand) {
                return@runLater
            }
            if (stand.brewingTime == 400) {
                stand.brewingTime = 400 - ticksLess
                stand.update()
                player.updateInventory()
            }
        }, 2)
    }
}