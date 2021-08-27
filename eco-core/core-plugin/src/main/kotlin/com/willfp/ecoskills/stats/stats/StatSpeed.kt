package com.willfp.ecoskills.stats.stats

import com.willfp.ecoskills.getStatLevel
import com.willfp.ecoskills.stats.Stat
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player

class StatSpeed : Stat(
    "speed"
) {
    override fun updateStatLevel(player: Player) {
        val modifier = AttributeModifier(
            this.uuid,
            this.name,
            (this.config.getDouble("percent-faster-per-level") * player.getStatLevel(this)) / 100.0,
            AttributeModifier.Operation.MULTIPLY_SCALAR_1
        )
        plugin.scheduler.run {
            val instance = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) ?: return@run
            instance.removeModifier(modifier)
            instance.addModifier(modifier)
        }
    }
}