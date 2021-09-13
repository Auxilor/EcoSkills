package com.willfp.ecoskills.skills.skills

import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.integrations.afk.isAfk
import com.willfp.ecoskills.skills.Skill
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent

class SkillArmory : Skill(
    "armory"
) {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handleLevelling(event: EntityDamageByEntityEvent) {
        val player = event.entity
        if (player !is Player) {
            return
        }

        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        if (plugin.configYml.getBool("skills.prevent-levelling-while-afk") && player.isAfk) {
            return
        }

        val xp = event.damage * this.config.getDouble("xp-per-hp")
        player.giveSkillExperience(this, xp)
    }
}