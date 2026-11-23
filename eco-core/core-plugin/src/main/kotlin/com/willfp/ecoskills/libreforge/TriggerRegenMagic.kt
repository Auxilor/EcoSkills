package com.willfp.ecoskills.libreforge

import com.willfp.ecoskills.api.event.PlayerRegenMagicEvent
import org.bukkit.event.EventHandler
import wiki.ecohub.game.entity.toDispatcher
import wiki.ecohub.rules.trigger.Trigger
import wiki.ecohub.rules.trigger.TriggerData
import wiki.ecohub.rules.trigger.TriggerParameter

object TriggerRegenMagic : Trigger("regen_magic") {
    override val description = "Fires when the player regenerates magic over time."

    override val categories = setOf("player")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VALUE to "The amount of magic regenerated."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerRegenMagicEvent) {
        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                event = event,
                value = event.amount.toDouble()
            )
        )
    }
}
