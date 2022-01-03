package com.willfp.ecoskills.effects.effects

import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.PotionUtils
import com.willfp.eco.util.StringUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.event.inventory.BrewEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType


class EffectPotionmaster : Effect(
    "potionmaster"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%percent_more%", NumberUtils.format(config.getDouble("percent-more-per-level") * level))
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: BrewEvent) {
        if (this.config.getStrings("disabled-in-worlds").contains(event.block.world.name)) {
            return
        }

        val player = event.contents.viewers.filterIsInstance<Player>().firstOrNull() ?: return

        if (player.getEffectLevel(this) == 0) {
            return
        }

        val multiplier = ((player.getEffectLevel(this) * this.config.getDouble("percent-more-per-level")) / 100) + 1

        this.plugin.scheduler.run {
            for (i in 0..2) {
                val item = event.contents.getItem(i) ?: continue

                val meta = item.itemMeta

                if (meta !is PotionMeta) {
                    continue
                }

                val potionData = meta.basePotionData

                when (potionData.type) {
                    PotionType.INSTANT_DAMAGE, PotionType.INSTANT_HEAL, PotionType.AWKWARD,
                    PotionType.MUNDANE, PotionType.THICK, PotionType.WATER -> continue
                    else -> {
                    }
                }

                val duration = PotionUtils.getDuration(potionData)
                val delta = (duration * multiplier).toInt() - duration
                val secondsDelta = NumberUtils.format(delta / 20.0)

                meta.persistentDataContainer.set(
                    plugin.namespacedKeyFactory.create("duration-delta"),
                    PersistentDataType.INTEGER,
                    delta
                )

                val lore = meta.lore ?: ArrayList()

                for (string in config.getStrings("lore", false)) {
                    lore.add(StringUtils.format(string.replace("%seconds%", secondsDelta), player))
                }

                meta.lore = lore

                item.itemMeta = meta
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handlePotionDelta(event: PlayerItemConsumeEvent) {
        val player = event.player

        val item = event.item

        val meta = item.itemMeta

        if (meta !is PotionMeta) {
            return
        }

        val delta = getDelta(meta)

        val data = meta.basePotionData

        val effects = mutableMapOf<PotionEffectType, Int>()

        if (data.type == PotionType.TURTLE_MASTER) {
            effects[PotionEffectType.SLOW] = 4
            effects[PotionEffectType.DAMAGE_RESISTANCE] = 2
        } else {
            val effectType = data.type.effectType ?: return
            effects[effectType] = if (data.type.isUpgradeable) 2 else 1
        }

        for ((k, v) in effects) {
            player.addPotionEffect(
                PotionEffect(
                    k,
                    PotionUtils.getDuration(data) + delta,
                    v
                )
            )
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handlePotionDelta(event: PotionSplashEvent) {
        val entities = event.affectedEntities

        val item = event.potion.item

        val meta = item.itemMeta

        if (meta !is PotionMeta) {
            return
        }

        val delta = getDelta(meta)

        val data = meta.basePotionData

        val effects = mutableMapOf<PotionEffectType, Int>()

        if (data.type == PotionType.TURTLE_MASTER) {
            effects[PotionEffectType.SLOW] = 4
            effects[PotionEffectType.DAMAGE_RESISTANCE] = 2
        } else {
            effects[data.type.effectType ?: return] = if (data.type.isUpgradeable) 2 else 1
        }

        for (entity in entities) {
            for ((key, value) in effects) {
                entity.addPotionEffect(
                    PotionEffect(
                        key,
                        ((PotionUtils.getDuration(data) + delta) * event.getIntensity(entity)).toInt(),
                        value
                    )
                )
            }
        }
    }

    private fun getDelta(meta: PotionMeta): Int {
        return meta.persistentDataContainer.getOrDefault(
            plugin.namespacedKeyFactory.create("duration-delta"),
            PersistentDataType.INTEGER,
            0
        )
    }
}