package com.willfp.ecoskills.skills.skills

import com.willfp.eco.core.integrations.afk.AFKManager
import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.skills.Skill
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerFishEvent

class SkillFishing : Skill(
    "fishing"
) {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handleLevelling(event: PlayerFishEvent) {
        if (this.config.getStrings("disabled-in-worlds").contains(event.player.world.name)) {
            return
        }

        val player = event.player

        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        if (plugin.configYml.getBool("skills.prevent-levelling-while-afk") && AFKManager.isAfk(player)) {
            return
        }

        val expToDrop = event.expToDrop

        val xp = expToDrop * this.config.getDouble("xp-per-experience-dropped")
        player.giveSkillExperience(this, xp)
    }
}