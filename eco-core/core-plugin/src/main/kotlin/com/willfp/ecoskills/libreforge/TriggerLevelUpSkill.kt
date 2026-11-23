package com.willfp.ecoskills.libreforge

import com.willfp.ecoskills.api.event.PlayerSkillLevelUpEvent
import org.bukkit.event.EventHandler
import wiki.ecohub.game.entity.toDispatcher
import wiki.ecohub.rules.trigger.Trigger
import wiki.ecohub.rules.trigger.TriggerData
import wiki.ecohub.rules.trigger.TriggerParameter

object TriggerLevelUpSkill : Trigger("level_up_skill") {
    override val description = "Fires when the player levels up any skill."

    override val categories = setOf("player")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VALUE to "The new level of the skill after leveling up."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerSkillLevelUpEvent) {
        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                event = event,
                value = event.level.toDouble()
            )
        )
    }
}
