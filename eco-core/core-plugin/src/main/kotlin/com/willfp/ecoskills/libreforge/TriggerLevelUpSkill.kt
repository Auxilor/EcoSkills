package com.willfp.ecoskills.libreforge

import com.willfp.ecoskills.api.PlayerSkillLevelUpEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerLevelUpSkill : Trigger("level_up_skill") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )
    
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerSkillLevelUpEvent) {
        val player = event.player

        this.dispatch(
            player,
            TriggerData(
                player = player,
                location = player.location,
                event = event,
                value = event.level.toDouble()
            )
        )
    }
}
