package com.willfp.ecoskills.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.magic.MagicTypes
import com.willfp.ecoskills.magic.magic
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectMultiplyMagic : Effect<NoCompileData>("multiply_magic") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("type", "You must specify the magic type!")
        require("multiplier", "You must specify the multiplier!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val type = MagicTypes.getByID(config.getString("type").lowercase()) ?: return false

        player.magic[type] = (player.magic[type] * config.getDoubleFromExpression("multiplier", data)).toInt()

        return true
    }
}
