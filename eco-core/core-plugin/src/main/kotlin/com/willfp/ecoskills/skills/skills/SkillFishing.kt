package com.willfp.ecoskills.skills.skills

import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.skills.Skill
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerFishEvent

class SkillFishing : Skill(
    "fishing"
) {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handleLevelling(event: PlayerFishEvent) {
        val player = event.player
        val expToDrop = event.expToDrop

        val xp = expToDrop * this.config.getDouble("xp-per-experience-dropped")
        player.giveSkillExperience(this, xp)
    }
}