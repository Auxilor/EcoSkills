package com.willfp.ecoskills.effects.effects

import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerFishEvent

class EffectMagneticRod : Effect(
    "magnetic_rod"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%percentage%", NumberUtils.format(level * config.getDouble("speed-per-level")))
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: PlayerFishEvent) {
        if (this.config.getStrings("disabled-in-worlds").contains(event.player.world.name)) {
            return
        }

        if (!(event.state == PlayerFishEvent.State.CAUGHT_FISH || event.state == PlayerFishEvent.State.CAUGHT_ENTITY)) {
            return
        }

        val player = event.player

        if (!this.checkConditions(player)) {
            return
        }

        val level = player.getEffectLevel(this)

        if (level == 0) {
            return
        }

        val vector = player.eyeLocation.toVector()
            .clone()
            .subtract(event.caught?.location?.toVector() ?: return)
            .normalize()

        val add = config.getDouble("speed-per-level") * level / 100

        val result = vector.multiply(1 + add)

        plugin.scheduler.run {
            event.caught?.velocity = result
        }
    }
}