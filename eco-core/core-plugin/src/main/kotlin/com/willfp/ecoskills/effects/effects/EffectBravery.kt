package com.willfp.ecoskills.effects.effects

import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.entity.Boss
import org.bukkit.entity.ElderGuardian
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.persistence.PersistentDataType


class EffectBravery: Effect(
    "bravery"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%percent_less%", NumberUtils.format(config.getDouble("percent-less-per-level") * level))
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
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

        if (event.damager !is Boss && event.damager !is ElderGuardian
            && !event.damager.persistentDataContainer.has(NamespacedKeyUtils.create("ecobosses", "boss"), PersistentDataType.STRING)) {
            return
        }

        val level = player.getEffectLevel(this)

        var multiplier = config.getDouble("percent-less-per-level") * level
        multiplier /= 100
        multiplier = 1 - multiplier

        event.damage = event.damage * multiplier
    }
}