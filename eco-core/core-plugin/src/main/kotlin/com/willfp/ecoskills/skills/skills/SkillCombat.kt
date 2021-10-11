package com.willfp.ecoskills.skills.skills

import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.integrations.afk.isAfk
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.tryAsPlayer
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

class SkillCombat : Skill(
    "combat"
) {
    @EventHandler(priority = EventPriority.MONITOR)
    fun handleLevelling(event: EntityDeathByEntityEvent) {
        val player = event.killer.tryAsPlayer() ?: return

        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        if (plugin.configYml.getBool("skills.prevent-levelling-while-afk") && player.isAfk) {
            return
        }

        val xp = event.victim.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value * this.config.getDouble("xp-per-heart")
        player.giveSkillExperience(this, xp)
    }
}