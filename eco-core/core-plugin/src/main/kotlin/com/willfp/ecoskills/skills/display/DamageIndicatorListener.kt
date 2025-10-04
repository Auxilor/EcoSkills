package com.willfp.ecoskills.skills.display

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.impl.EmptyTestableEntity
import com.willfp.eco.core.integrations.hologram.HologramManager
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.randDouble
import com.willfp.eco.util.toNiceString
import com.willfp.ecoskills.skills.isSkillCrit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Allay
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityRegainHealthEvent

class DamageIndicatorListener(
    private val plugin: EcoPlugin
) : Listener {
    private val disabledEntities = plugin.configYml.getStrings("damage-indicators.disabled-for-entities")
        .map { Entities.lookup(it) }
        .filterNot { it is EmptyTestableEntity }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        if (!plugin.configYml.getBool("damage-indicators.enabled")) {
            return
        }

        val victim = event.entity

        if (event.entity == event.damager) {
            return
        }

        if (event.damage == 0.0) {
            return
        }

        if (victim is Player && victim.isBlocking) {
            return
        }

        if (disabledEntities.any { it.matches(victim) }) {
            return
        }

        val location = victim.location.clone()
            .add(0.0, victim.height, 0.0)
            .withHoloOffset()

        var text: String = if (event.isSkillCrit) {
            plugin.configYml.getString("damage-indicators.format.crit")
        } else {
            plugin.configYml.getString("damage-indicators.format.normal")
        }

        text = if (plugin.configYml.getBool("damage-indicators.final-damage")) {
            text.replace("%damage%", event.finalDamage.toNiceString())
        } else {
            text.replace("%damage%", event.damage.toNiceString())
        }.formatEco()

        val holo = HologramManager.createHologram(location, listOf(text))

        plugin.scheduler.runLater(30) {
            holo.remove()
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onHeal(event: EntityRegainHealthEvent) {
        if (!plugin.configYml.getBool("damage-indicators.healing.enabled")) {
            return
        }

        val entity = event.entity

        if (entity is Player && entity.gameMode == GameMode.SPECTATOR) {
            return
        }

        if (entity is Allay) {
            return
        }

        val location = entity.location.clone()
            .add(0.0, entity.height, 0.0)
            .withHoloOffset()

        val text = plugin.configYml.getString("damage-indicators.healing.format")
            .replace("%damage%", event.amount.toNiceString())
            .formatEco()

        val holo = HologramManager.createHologram(location, listOf(text))

        plugin.scheduler.runLater(30) {
            holo.remove()
        }
    }

    private fun Location.withHoloOffset(): Location {
        val x = plugin.configYml.getDouble("damage-indicators.max-x-offset")
        val y = plugin.configYml.getDouble("damage-indicators.max-y-offset")
        val z = plugin.configYml.getDouble("damage-indicators.max-z-offset")

        return this.add(
            randDouble(-x, x),
            randDouble(-y, y),
            randDouble(-z, z)
        )
    }
}
