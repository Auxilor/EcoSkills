package com.willfp.ecoskills.libreforge

import com.willfp.ecoskills.api.event.PlayerSkillXPGainEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerGainSkillXp : Trigger("gain_skill_xp") {
    override val description = "Fires when the player gains xp for any skill."

    override val categories = setOf("player")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VALUE to "The amount of xp gained."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerSkillXPGainEvent) {
        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                event = event,
                value = event.gainedXP
            )
        )
    }
}
