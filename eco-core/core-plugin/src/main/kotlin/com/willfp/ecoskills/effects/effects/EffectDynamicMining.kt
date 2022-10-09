package com.willfp.ecoskills.effects.effects

import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class EffectDynamicMining : Effect(
    "dynamic_mining"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%chance%", NumberUtils.format(config.getDouble("chance-per-level") * level))
            .replace(
                "%seconds%",
                NumberUtils.format(((config.getInt("ticks-per-level") * level) + config.getInt("base-ticks")) / 20.0)
            )
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: BlockBreakEvent) {
        if (this.config.getStrings("disabled-in-worlds").contains(event.block.world.name)) {
            return
        }

        val player = event.player

        if (!this.checkConditions(player)) {
            return
        }

        val level = player.getEffectLevel(this)

        if (NumberUtils.randFloat(0.0, 100.0) >= config.getDouble("chance-per-level") * level) {
            return
        }

        val duration = (config.getInt("ticks-per-level") * level) + config.getInt("base-ticks")

        player.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, duration, config.getInt("level")))
    }
}