package com.willfp.ecoskills.effects.effects

import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.items.Items
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.inventory.ItemStack

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

        if (!this.checkConditions(player)) {
            return
        }

        val level = player.getEffectLevel(this)

        val chance = config.getDouble("chance-per-level") * level

        if (NumberUtils.randFloat(0.0, 100.0) >= chance) {
            return
        }

        val items = config.getStrings("rare-loot-items")

        if (items.isEmpty()) {
            return
        }

        val reward = if (items.size == 1) items.first() else items[NumberUtils.randInt(0, items.size - 1)]

        LootReward.fromString(reward).reward(player, event.caught!!.location)
    }

    class LootReward(
        private val item: ItemStack? = null,
        private val command: String? = null
    )
    {
        companion object {
            @JvmStatic
            fun fromString(from: String): LootReward {
                return if (from.startsWith("command::", true)) {
                    LootReward(command = from.replace("command::", "").replace("command:: ", ""))
                } else {
                    LootReward(item = Items.lookup(from).item)
                }
            }
        }

        fun reward(player: Player, location: Location = player.location) {
            if (item != null) {
                DropQueue(player)
                    .setLocation(location)
                    .addItem(item)
                    .push()
            }
            else {
                command?.let { Bukkit.dispatchCommand(Bukkit.getConsoleSender(), it.replace("%player%", player.name)) }
            }

        }
    }
}

