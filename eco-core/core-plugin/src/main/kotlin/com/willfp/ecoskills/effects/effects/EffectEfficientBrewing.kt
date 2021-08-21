package com.willfp.ecoskills.effects.effects

import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.StringUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import com.willfp.ecoskills.util.PotionUtils
import org.bukkit.Bukkit
import org.bukkit.block.BrewingStand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.event.inventory.BrewEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType


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

        if (event.inventory.holder !is BrewingStand) {
            return
        }

        val stand = event.inventory.holder as BrewingStand

        this.plugin.scheduler.runLater({
            if (stand.brewingTime == 400) {
                val multiplier = ((player.getEffectLevel(this) * this.config.getDouble("percent-faster-per-level")) / 100) + 1
                stand.brewingTime = (stand.brewingTime / multiplier).toInt()
                this.plugin.runnableFactory.create { task ->
                    if (stand.brewingTime - 20 <= 0) {
                        stand.brewingTime = 1
                        task.cancel();
                    }
                    stand.brewingTime = stand.brewingTime - 20
                }.runTaskTimer(0, 2)
            }
        }, 2)
    }
}