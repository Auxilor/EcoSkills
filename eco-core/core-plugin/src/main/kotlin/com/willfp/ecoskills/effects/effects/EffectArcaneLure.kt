package com.willfp.ecoskills.effects.effects

import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.items.Items
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.Color
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class EffectArcaneLure: Effect(
        "arcane_lure"
){
    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%chance%", NumberUtils.format(config.getDouble("chance-per-level") * level))
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: PlayerFishEvent){
        val player = event.player

        if (event.state != PlayerFishEvent.State.CAUGHT_FISH) {
            return
        }

        val level = player.getEffectLevel(this)

        val chance = config.getDouble("chance-per-level") * level

        if (NumberUtils.randFloat(0.0, 100.0) > chance) {
            return
        }

        val potion = Items.lookup("potion").item
        val pMeta = potion.itemMeta as PotionMeta

        val potionTypes = arrayOf<String>(
                "FIRE_RESISTANCE",
                "JUMP",
                "INVISIBILITY",
                "NIGHT_VISION",
                "REGEN",
                "SLOWNESS",
                "SPEED",
                "STRENGTH",
                "SLOW_FALLING",
                "WATER_BREATHING",
                "POISON",
                "SLOWNESS"
        )

        val selectedType = potionTypes[NumberUtils.randInt(0, potionTypes.size - 1)]
        val pEffect = PotionEffectType.getByName(selectedType)?.let { PotionEffect(it, 6000, 0) }
        if (pEffect != null) {
            pMeta.addCustomEffect(pEffect, true)
            pMeta.setDisplayName("Ancient Potion")
            pMeta.lore = listOf<String>("A mysterious potion drawn from the deep...")
            pMeta.color = Color.fromRGB(135,243,255) // Electric Blue
            potion.itemMeta = pMeta
        }
        else {
            pMeta.addCustomEffect(PotionEffect(PotionEffectType.WEAKNESS, 3000, 0), true)
            pMeta.setDisplayName("Ancient Potion")
            pMeta.lore = listOf<String>("A mysterious potion drawn from the deep...")
            pMeta.color = Color.fromRGB(135,243,255) // Electric Blue
            potion.itemMeta = pMeta
        }

        DropQueue(player)
                .setLocation(event.player.location)
                .addItem(potion)
                .push()
    }
}
