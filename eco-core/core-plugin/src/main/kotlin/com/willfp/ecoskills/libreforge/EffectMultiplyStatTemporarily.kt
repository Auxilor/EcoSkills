package com.willfp.ecoskills.libreforge

import com.willfp.ecoskills.api.addStatModifier
import com.willfp.ecoskills.api.modifiers.ModifierOperation
import com.willfp.ecoskills.api.modifiers.StatModifier
import com.willfp.ecoskills.api.removeStatModifier
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.stats.Stats
import wiki.ecohub.config.ArgType
import wiki.ecohub.config.Config
import wiki.ecohub.config.arguments
import wiki.ecohub.config.section.getDoubleFromExpression
import wiki.ecohub.config.section.getIntFromExpression
import wiki.ecohub.core.registry.NoCompileData
import wiki.ecohub.rules.effect.Effect
import wiki.ecohub.rules.effect.RunOrder
import wiki.ecohub.rules.trigger.TriggerData
import wiki.ecohub.rules.trigger.TriggerParameter
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
            type = ArgType.EXPRESSION,
            example = "1 + %level% * 0.1"
        )
        require(
            "duration",
            "You must specify the duration for the boost!",
            description = "The duration in ticks that the boost lasts for.",
            type = ArgType.EXPRESSION,
            example = "20 * %level%"
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

        plugin.scheduler.global().runLater(config.getIntFromExpression("duration", data).toLong()) {
            player.removeStatModifier(uuid)
        }

        return true
    }
}
