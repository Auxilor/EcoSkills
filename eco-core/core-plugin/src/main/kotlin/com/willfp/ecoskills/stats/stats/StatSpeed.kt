package com.willfp.ecoskills.stats.stats

import com.willfp.eco.util.toNiceString
import com.willfp.ecoskills.getStatLevel
import com.willfp.ecoskills.stats.Stat
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerChangedWorldEvent

class StatSpeed : Stat(
    "speed"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace(
            "%percent_faster%",
            (this.config.getDouble("percent-faster-per-level") * level).toNiceString()
        )
    }

    override fun updateStatLevel(player: Player) {
        val modifier = AttributeModifier(
            this.uuid,
            this.name,
            (this.config.getDouble("percent-faster-per-level") * 0) / 100.0,
            AttributeModifier.Operation.MULTIPLY_SCALAR_1
        )
        val instance = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) ?: return

        instance.removeModifier(modifier)

        if (this.config.getStrings("disabled-in-worlds").contains(player.world.name)) {
            return
        }

        plugin.scheduler.run {
            instance.removeModifier(modifier)
            instance.addModifier(modifier)
        }

        val maxSpeed = 1 + (this.config.getDouble("percent-faster-per-level") * player.getStatLevel(this)) / 100.0
        player.walkSpeed = (maxSpeed / 5.0).toFloat()
        player.sendMessage("§aTvá rychlost chůze byla nastavena na §e${String.format("%.1f", maxSpeed * 100)}% §7(/walkspeed)")
    }

    @EventHandler(priority = EventPriority.LOW)
    fun handle(event: PlayerChangedWorldEvent) {
        val player = event.player

        plugin.scheduler.run {
            updateStatLevel(player)
        }
    }
}