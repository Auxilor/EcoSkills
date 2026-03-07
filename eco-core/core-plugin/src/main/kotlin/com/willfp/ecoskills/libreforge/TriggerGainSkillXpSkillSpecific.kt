package com.willfp.ecoskills.libreforge

import com.willfp.ecoskills.api.event.PlayerSkillXPGainEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

abstract class TriggerGainSkillXpSkillSpecific(
    private val skillId: String
) : Trigger("gain_skill_xp_$skillId") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerSkillXPGainEvent) {
        if (!event.skill.id.equals(skillId, ignoreCase = true)) {
            return
        }

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

object TriggerGainSkillXpAlchemy : TriggerGainSkillXpSkillSpecific("alchemy")
object TriggerGainSkillXpCombat : TriggerGainSkillXpSkillSpecific("combat")
object TriggerGainSkillXpEnchanting : TriggerGainSkillXpSkillSpecific("enchanting")
object TriggerGainSkillXpFarming : TriggerGainSkillXpSkillSpecific("farming")
object TriggerGainSkillXpFishing : TriggerGainSkillXpSkillSpecific("fishing")
object TriggerGainSkillXpForaging : TriggerGainSkillXpSkillSpecific("foraging")
object TriggerGainSkillXpMining : TriggerGainSkillXpSkillSpecific("mining")
