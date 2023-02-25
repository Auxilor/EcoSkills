package com.willfp.ecoskills.stats.stats

import com.willfp.eco.util.toNiceString
import com.willfp.ecoskills.getStatLevel
import com.willfp.ecoskills.isStatEnabled
import com.willfp.ecoskills.stats.Stat
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerChangedWorldEvent

class StatHealth : Stat(
    "health"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace(
            "%health%",
            (this.config.getDouble("health-per-level") * level).toNiceString()
        )
    }

    override fun updateStatLevel(player: Player) {
        val modifier = AttributeModifier(
            this.uuid,
            this.name,
            this.config.getDouble("health-per-level") * player.getStatLevel(this),
            AttributeModifier.Operation.ADD_NUMBER
        )
        val instance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH) ?: return

        instance.removeModifier(modifier)

        if (this.config.getStrings("disabled-in-worlds").contains(player.world.name)) {
            return
        }

        plugin.scheduler.run {
            instance.removeModifier(modifier)
            if (player.isStatEnabled(this)) {
                instance.addModifier(modifier)
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    fun handle(event: PlayerChangedWorldEvent) {
        val player = event.player

        plugin.scheduler.run {
            updateStatLevel(player)
        }
    }
}