package com.willfp.ecoskills.skills.skills

import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.skills.Skill
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

class SkillCombat : Skill(
    "combat"
) {
    @EventHandler(priority = EventPriority.HIGH)
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

        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        val xp = event.victim.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value * this.config.getDouble("xp-per-heart")
        player.giveSkillExperience(this, xp)
    }
}