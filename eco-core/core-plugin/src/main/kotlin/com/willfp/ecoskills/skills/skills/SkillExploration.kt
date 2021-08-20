package com.willfp.ecoskills.skills.skills

import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.api.PlayerSkillExpGainEvent
import com.willfp.ecoskills.getStatLevel
import com.willfp.ecoskills.giveSkillExperience
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
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerMoveEvent
import java.util.*

class SkillExploration : Skill(
    "exploration"
) {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handleLevelling(event: PlayerMoveEvent) {
        val player = event.player

        val from = event.from
        val to = event.to ?: return

        if (from.world != to.world) {
            return
        }

        val speed = from.distance(to) / 0.2158
        var xp = this.config.getDouble("base-xp-to-give")
        if (this.config.getBool("multiply-xp-by-speed")) {
            xp *= speed
        }

        if (NumberUtils.randFloat(0.0, 100.0) < this.config.getDouble("xp-on-move-chance")) {
            player.giveSkillExperience(this, xp)
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handleLevelling(event: EntityDamageEvent) {
        if (event.entity !is Player) {
            return
        }

        if (event.cause != EntityDamageEvent.DamageCause.FALL) {
            return
        }

        val player = event.entity as Player

        val xp = this.config.getDouble("fall-damage-xp-per-hp") * event.finalDamage
        player.giveSkillExperience(this, xp)
    }
}