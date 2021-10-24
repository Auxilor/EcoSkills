package com.willfp.ecoskills.stats

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.StringUtils
import com.willfp.ecoskills.integrations.hologram.HologramManager
import com.willfp.ecoskills.isCrit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class DamageIndicatorListener(
    private val plugin: EcoPlugin
) : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        if (!plugin.configYml.getBool("damage-indicators.enabled")) {
            return
        }

        if (event.entity == event.damager) {
            return
        }

        val location = event.entity.location

        location.add(0.0, event.entity.height, 0.0)

        val x = plugin.configYml.getDouble("damage-indicators.max-x-offset")
        val y = plugin.configYml.getDouble("damage-indicators.max-y-offset")
        val z = plugin.configYml.getDouble("damage-indicators.max-z-offset")
        location.add(
            NumberUtils.randFloat(-x, x),
            NumberUtils.randFloat(-y, y),
            NumberUtils.randFloat(-z, z)
        )

        var text: String = if (event.isCrit) {
            plugin.configYml.getString("damage-indicators.format.crit", false)
        } else {
            plugin.configYml.getString("damage-indicators.format.normal", false)
        }

        text = text.replace("%damage%", NumberUtils.format(event.damage))

        text = StringUtils.format(text)

        HologramManager.spawnHolo(location, listOf(text), 30);
    }
}