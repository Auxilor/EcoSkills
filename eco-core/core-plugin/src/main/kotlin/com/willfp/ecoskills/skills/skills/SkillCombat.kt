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
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.util.*

class SkillCombat : Skill(
    "mining"
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

    @EventHandler(priority = EventPriority.LOW)
    fun handleLevelling(event: EntityDeathByEntityEvent) {
        var player = event.killer

        if (player is Projectile) {
            if (player.shooter !is Player) {
                return
            } else {
                player = player.shooter as Player
            }
        }

        if (player !is Player) {
            return
        }

        val xp = event.victim.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value * this.config.getDouble("xp-per-heart")
        val gainEvent = PlayerSkillExpGainEvent(player, this, xp)
        Bukkit.getPluginManager().callEvent(gainEvent)
    }
}