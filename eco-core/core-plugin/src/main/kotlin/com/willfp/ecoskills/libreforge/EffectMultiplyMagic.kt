package com.willfp.ecoskills.libreforge

import com.willfp.ecoskills.magic.MagicTypes
import com.willfp.ecoskills.magic.magic
import wiki.ecohub.config.ArgType
import wiki.ecohub.config.Config
import wiki.ecohub.config.arguments
import wiki.ecohub.config.section.getDoubleFromExpression
import wiki.ecohub.core.registry.NoCompileData
import wiki.ecohub.rules.effect.Effect
import wiki.ecohub.rules.trigger.TriggerData
import wiki.ecohub.rules.trigger.TriggerParameter

object EffectMultiplyMagic : Effect<NoCompileData>("multiply_magic") {
    override val description = "Multiplies the player's current magic of the given type by the multiplier."

    override val categories = setOf("player")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "type",
            "You must specify the magic type!",
            description = "The magic type to multiply.",
            type = ArgType.STRING
        )
        require(
            "multiplier",
            "You must specify the multiplier!",
            description = "The multiplier to apply to the player's current magic.",
            type = ArgType.EXPRESSION,
            example = "1 + %level% * 0.1"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val type = MagicTypes.getByID(config.getString("type").lowercase()) ?: return false

        player.magic[type] = (player.magic[type] * config.getDoubleFromExpression("multiplier", data)).toInt()

        return true
    }
}
