package com.willfp.ecoskills.skills.skills

import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.skills.Skill
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerFishEvent

class SkillFishing : Skill(
    "fishing"
) {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handleLeveling(event: PlayerFishEvent) {
        val player = event.player.filterSkillEnabled() ?: return

        val expToDrop = event.expToDrop

        if (expToDrop == 0) {
            return
        }

        val xp = expToDrop * this.config.getDouble("xp-per-experience-dropped")
        player.giveSkillExperience(this, xp)
    }
}