package com.willfp.ecoskills.libreforge

import com.willfp.ecoskills.mana.MagicType
import com.willfp.ecoskills.mana.magic
import com.willfp.libreforge.ConfigurableElement
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.arguments.EffectArgument
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.DispatchedTrigger

class EffectArgumentMagicCost(private val type: MagicType) : EffectArgument<NoCompileData>("${type.id}_cost") {
    override fun isMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: NoCompileData): Boolean {
        val cost = element.config.getIntFromExpression("${type.id}_cost", trigger.data)
        val player = trigger.player

        return player.magic[type] >= cost
    }

    override fun ifMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: NoCompileData) {
        val cost = element.config.getIntFromExpression("${type.id}_cost", trigger.data)
        val player = trigger.player

        player.magic[type] -= cost
    }
}
