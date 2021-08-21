package com.willfp.ecoskills.effects.effects

import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.Bukkit
import org.bukkit.block.BrewingStand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerRecipeDiscoverEvent
import org.bukkit.potion.PotionType


class EffectMysticResilience : Effect(
    "mystic_resilience"
) {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: EntityPotionEffectEvent) {
        val player = event.entity

        if (player !is Player) {
            return
        }

        val level = player.getEffectLevel(this)

        if (NumberUtils.randFloat(0.0, 100.0) >= this.config.getDouble("chance-per-level") * level) {
            return
        }

        val effect = event.newEffect ?: return

        when (effect.type.name) {
            "BLINDNESS",
            "CONFUSION",
            "HARM",
            "HUNGER",
            "POISON",
            "WITHER",
            "LEVITATION",
            "SLOW_DIGGING",
            "WEAKNESS" -> event.isCancelled = true
        }
    }
}