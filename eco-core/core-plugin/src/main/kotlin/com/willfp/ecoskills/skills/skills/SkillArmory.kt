package com.willfp.ecoskills.skills.skills

import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.skills.Skill
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent

class SkillArmory : Skill(
    "armory"
) {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handleLevelling(event: EntityDamageByEntityEvent) {
        val player = (event.entity as? Player)?.filterSkillEnabled() ?: return

        if (player.isBlocking) {
            return
        }

        if (player.health <= event.finalDamage) {
            return
        }

        val xp = (event.finalDamage * this.config.getDouble("xp-per-hp"))
            .coerceAtMost(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0)


        player.giveSkillExperience(this, xp)
    }
}
