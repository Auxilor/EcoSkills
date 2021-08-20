package com.willfp.ecoskills.skills.skills

import com.willfp.ecoskills.api.PlayerSkillExpGainEvent
import com.willfp.ecoskills.skills.Skill
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerFishEvent
import java.util.*

class SkillFishing : Skill(
    "fishing"
) {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handleLevelling(event: PlayerFishEvent) {
        val player = event.player
        val expToDrop = event.expToDrop

        if (player !is Player) {
            return
        }

        val xp = expToDrop * this.config.getDouble("xp-per-experience-dropped")
        val gainEvent = PlayerSkillExpGainEvent(player, this, xp)
        Bukkit.getPluginManager().callEvent(gainEvent)
    }
}