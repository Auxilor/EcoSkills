package com.willfp.ecoskills.effects.effects

import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.items.Items
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerFishEvent

class EffectEyeOfTheDepths: Effect(
    "eye_of_the_depths"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%chance%", NumberUtils.format(config.getDouble("chance-per-level") * level))
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: PlayerFishEvent) {
        if (this.config.getStrings("disabled-in-worlds").contains(event.player.world.name)) {
            return
        }

        val player = event.player

        if (event.state != PlayerFishEvent.State.CAUGHT_FISH) {
            return
        }

        val level = player.getEffectLevel(this)

        val chance = config.getDouble("chance-per-level") * level

        if (NumberUtils.randFloat(0.0, 100.0) >= chance) {
            return
        }

        val items = config.getStrings("rare-loot-items", false)

        val itemName = items[NumberUtils.randInt(0, items.size - 1)]

        val item = Items.lookup(itemName).item

        DropQueue(player)
            .setLocation(event.caught!!.location)
            .addItem(item)
            .push()
    }
}