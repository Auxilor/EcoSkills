package com.willfp.ecoskills.actionbar

import com.willfp.eco.core.EcoPlugin
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityRegainHealthEvent

class HealthScaleListener(
    private val plugin: EcoPlugin
) : Listener {
    @EventHandler
    fun scaleHealth(event: EntityRegainHealthEvent) {
        val player = event.entity as? Player ?: return

        if (!plugin.configYml.getBool("persistent-action-bar.scale-health")) {
            return
        }

        player.isHealthScaled = true
        player.healthScale = 20.0
    }

    @EventHandler
    fun scaleHealth(event: EntityDamageEvent) {
        val player = event.entity as? Player ?: return

        if (!plugin.configYml.getBool("persistent-action-bar.scale-health")) {
            return
        }

        player.isHealthScaled = true
        player.healthScale = 20.0
    }
}