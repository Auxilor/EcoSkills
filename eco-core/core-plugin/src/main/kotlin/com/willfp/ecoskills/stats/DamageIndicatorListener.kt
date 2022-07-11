package com.willfp.ecoskills.stats

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.integrations.hologram.HologramManager
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.StringUtils
import com.willfp.ecoskills.isCrit
import org.bukkit.GameMode
import org.bukkit.entity.Allay
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityRegainHealthEvent

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

        if (event.damager is Projectile && (event.damager as Projectile).shooter == event.entity) {
            return
        }

        if (event.damage == 0.0) {
            return
        }

        if (event.entity is Player && (event.entity as Player).isBlocking) {
            return
        }

        if (Prerequisite.HAS_1_19.isMet) {
            if (event.entity is Allay) {
                return
            }
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
            plugin.configYml.getString("damage-indicators.format.crit")
        } else {
            plugin.configYml.getString("damage-indicators.format.normal")
        }

        text = if (plugin.configYml.getBool("damage-indicators.final-damage")) {
            text.replace("%damage%", NumberUtils.format(event.finalDamage))
        } else {
            text.replace("%damage%", NumberUtils.format(event.damage))
        }


        text = StringUtils.format(text)

        val holo = HologramManager.createHologram(location, listOf(text))

        plugin.scheduler.runLater({
            holo.remove()
        }, 30)
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onHeal(event: EntityRegainHealthEvent) {
        if (!plugin.configYml.getBool("damage-indicators.healing.enabled")) {
            return
        }

        if (event.entity is Player && (event.entity as Player).gameMode == GameMode.SPECTATOR) {
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

        var text = plugin.configYml.getString("damage-indicators.healing.format")

        text = text.replace("%damage%", NumberUtils.format(event.amount))

        text = StringUtils.format(text)

        val holo = HologramManager.createHologram(location, listOf(text))

        plugin.scheduler.runLater({
            holo.remove()
        }, 30)
    }
}