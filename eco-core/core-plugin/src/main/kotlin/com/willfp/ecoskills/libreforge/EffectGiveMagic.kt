package com.willfp.ecoskills.libreforge

import com.willfp.ecoskills.magic.MagicTypes
import com.willfp.ecoskills.magic.magic
import wiki.ecohub.config.ArgType
import wiki.ecohub.config.Config
import wiki.ecohub.config.arguments
import wiki.ecohub.config.section.getIntFromExpression
import wiki.ecohub.core.registry.NoCompileData
import wiki.ecohub.rules.effect.Effect
import wiki.ecohub.rules.trigger.TriggerData
import wiki.ecohub.rules.trigger.TriggerParameter

object EffectGiveMagic : Effect<NoCompileData>("give_magic") {
    override val description = "Gives the player a flat amount of magic of the given type."

    override val categories = setOf("player")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "type",
            "You must specify the magic type!",
            description = "The magic type to give.",
            type = ArgType.STRING
        )
        require(
            "amount",
            "You must specify the amount to add / subtract!",
            description = "The amount of magic to add. Use a negative value to subtract.",
            type = ArgType.EXPRESSION,
            example = "10 + %level%"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val type = MagicTypes.getByID(config.getString("type").lowercase()) ?: return false

        player.magic[type] += config.getIntFromExpression("amount", data)

        return true
    }
}
