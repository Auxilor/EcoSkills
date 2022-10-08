package com.willfp.ecoskills.effects.effects

import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.inventory.meta.Damageable

class EffectSecondChance: Effect(
    "second_chance"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%chance%", NumberUtils.format(config.getDouble("chance-per-level") * level))
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: PlayerItemDamageEvent) {
        if (this.config.getStrings("disabled-in-worlds").contains(event.player.world.name)) {
            return
        }

        val player = event.player
        val item = event.item
        val meta = item.itemMeta

        if (!this.checkConditions(player)) {
            return
        }

        if (meta !is Damageable) {
            return
        }

        if (item.type.maxDurability - meta.damage > this.config.getInt("below-durability-check")) {
            return
        }

        val level = player.getEffectLevel(this)

        if (NumberUtils.randFloat(0.0, 100.0) < config.getDouble("chance-per-level") * level) {
            meta.damage = 0
            event.isCancelled = true
        }

        item.itemMeta = meta
    }
}