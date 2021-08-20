package com.willfp.ecoskills.stats.stats

import com.willfp.ecoskills.getStatLevel
import com.willfp.ecoskills.stats.Stat
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent

class StatSpeed : Stat(
    "speed"
) {
    override fun updateStatLevel(player: Player) {
        val modifier = AttributeModifier(
            this.uuid,
            this.name,
            (this.config.getDouble("percent-faster-per-level") * player.getStatLevel(this)) / 100,
            AttributeModifier.Operation.MULTIPLY_SCALAR_1
        )
        val instance = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) ?: return
        instance.removeModifier(modifier)
        plugin.scheduler.runLater({
            instance.addModifier(modifier)
        }, 1)
    }
}