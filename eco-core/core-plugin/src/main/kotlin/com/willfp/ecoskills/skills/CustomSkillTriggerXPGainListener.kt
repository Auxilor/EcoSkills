package com.willfp.ecoskills.skills

import com.willfp.ecoskills.giveSkillExperience
import com.willfp.libreforge.events.TriggerPreProcessEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class CustomSkillTriggerXPGainListener : Listener {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: TriggerPreProcessEvent) {
        val player = event.player

        for (skill in CustomSkills.values()) {
            if (!skill.isEnabledFor(player)) {
                continue
            }

            val amount = skill.getXP(event)

            if (amount > 0.0) {
                player.giveSkillExperience(skill, amount)
            }
        }
    }
}
