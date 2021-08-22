package com.willfp.ecoskills.effects.effects

import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import java.util.*

class EffectAcceleratedEscape: Effect(
    "accelerated_escape"
) {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: EntityDamageEvent) {
        val player = event.entity

        if (player !is Player) {
            return
        }

        val level = player.getEffectLevel(this)

        val modifier = AttributeModifier(
            UUID.randomUUID(),
            NumberUtils.randFloat(0.0, 1.0).toString(),
            (config.getDouble("percent-faster-per-level") * level) / 100,
            AttributeModifier.Operation.MULTIPLY_SCALAR_1
        )

        val inst = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!

        inst.addModifier(modifier)

        plugin.scheduler.runLater({
            inst.removeModifier(modifier)
        }, this.config.getInt("ticks").toLong())
    }
}