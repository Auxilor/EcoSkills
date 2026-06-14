package com.willfp.ecoskills.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.magic.MagicTypes
import com.willfp.ecoskills.magic.magic
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

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
            type = ArgType.EXPRESSION
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val type = MagicTypes.getByID(config.getString("type").lowercase()) ?: return false

        player.magic[type] += config.getIntFromExpression("amount", data)

        return true
    }
}
