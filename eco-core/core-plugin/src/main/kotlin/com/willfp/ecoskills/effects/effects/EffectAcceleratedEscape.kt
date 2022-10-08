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

class EffectAcceleratedEscape: Effect(
    "accelerated_escape"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%percent_faster%", NumberUtils.format(config.getDouble("percent-faster-per-level") * level))
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: EntityDamageEvent) {
        if (this.config.getStrings("disabled-in-worlds").contains(event.entity.world.name)) {
            return
        }

        val player = event.entity

        if (player !is Player) {
            return
        }

        if (!this.checkConditions(player)) {
            return
        }

        val level = player.getEffectLevel(this)

        val modifier = AttributeModifier(
            this.uuid,
            this.id,
            (config.getDouble("percent-faster-per-level") * level) / 100.0,
            AttributeModifier.Operation.MULTIPLY_SCALAR_1
        )

        val inst = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!

        inst.removeModifier(modifier)
        inst.addModifier(modifier)

        plugin.scheduler.runLater({
            inst.removeModifier(modifier)
        }, this.config.getInt("ticks").toLong())
    }
}