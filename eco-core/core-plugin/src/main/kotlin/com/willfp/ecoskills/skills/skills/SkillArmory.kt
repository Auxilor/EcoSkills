package com.willfp.ecoskills.skills.skills

import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.skills.Skill
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent

class SkillArmory : Skill(
    "armory"
) {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handleLeveling(event: EntityDamageByEntityEvent) {
        val player = (event.entity as? Player)?.filterSkillEnabled() ?: return

        if (player.isBlocking) {
            return
        }

        val xp = event.damage * this.config.getDouble("xp-per-hp")
        player.giveSkillExperience(this, xp)
    }
}