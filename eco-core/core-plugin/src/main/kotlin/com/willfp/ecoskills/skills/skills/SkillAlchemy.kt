package com.willfp.ecoskills.skills.skills

import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.ecoskills.api.PlayerSkillExpGainEvent
import com.willfp.ecoskills.getStatLevel
import com.willfp.ecoskills.skills.Skill
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.BrewEvent
import java.util.*

class SkillAlchemy : Skill(
    "alchemy"
) {
    private val rewards: MutableMap<Material, Double>

    init {
        rewards = EnumMap(org.bukkit.Material::class.java)
    }

    override fun postUpdate() {
        rewards.clear()
        for (string in this.config.getStrings("xp-rewards", false)) {
            val split = string.split(":")
            val material = Material.getMaterial(split[0].uppercase()) ?: continue
            rewards[material] = split[1].toDouble()
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handleLevelling(event: BrewEvent) {
        var player: Player? = null

        for (viewer in event.contents.viewers) {
            if (viewer is Player) {
                player = viewer
                break
            }
        }

        if (player == null) {
            return
        }

        val type = event.contents.ingredient?.type ?: return

        val toGive = rewards[type] ?: return
        val gainEvent = PlayerSkillExpGainEvent(player, this, toGive)
        Bukkit.getPluginManager().callEvent(gainEvent)
    }
}