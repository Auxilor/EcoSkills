package com.willfp.ecoskills.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.api.addStatModifier
import com.willfp.ecoskills.api.modifiers.ModifierOperation
import com.willfp.ecoskills.api.modifiers.StatModifier
import com.willfp.ecoskills.api.removeStatModifier
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.stats.Stats
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.RunOrder
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import java.util.UUID

object EffectMultiplyStatTemporarily : Effect<NoCompileData>("multiply_stat_temporarily") {
    override val description = "Temporarily multiplies one of the player's stats by the given multiplier for a fixed duration."

    override val categories = setOf("player")

    override val runOrder = RunOrder.START // Not sure if this is necessary

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "stat",
            "You must specify the stat!",
            description = "The stat to multiply.",
            type = ArgType.STRING
        )
        require(
            "multiplier",
            "You must specify the multiplier!",
            description = "The multiplier to apply to the stat.",
            type = ArgType.EXPRESSION
        )
        require(
            "duration",
            "You must specify the duration for the boost!",
            description = "The duration in ticks that the boost lasts for.",
            type = ArgType.EXPRESSION
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val stat = Stats.getByID(config.getString("stat")) ?: return false
        val multiplier = config.getDoubleFromExpression("multiplier", data)
        val uuid = UUID.randomUUID()

        player.addStatModifier(
            StatModifier(
                uuid,
                stat,
                multiplier,
                ModifierOperation.MULTIPLY
            )
        )

        plugin.scheduler.runLater(config.getIntFromExpression("duration", data).toLong()) {
            player.removeStatModifier(uuid)
        }

        return true
    }
}
