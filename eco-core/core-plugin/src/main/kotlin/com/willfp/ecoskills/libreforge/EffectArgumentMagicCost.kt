package com.willfp.ecoskills.libreforge

import com.willfp.ecoskills.magic.MagicType
import com.willfp.ecoskills.magic.magic
import com.willfp.libreforge.ConfigurableElement
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.arguments.EffectArgument
import com.willfp.libreforge.get
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.DispatchedTrigger
import org.bukkit.entity.Player

class EffectArgumentMagicCost(private val type: MagicType) : EffectArgument<NoCompileData>("${type.id}_cost") {
    override fun isMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: NoCompileData): Boolean {
        val player = trigger.dispatcher.get<Player>() ?: return false

        val cost = element.config.getIntFromExpression("${type.id}_cost", trigger.data)

        return player.magic[type] >= cost
    }

    override fun ifMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: NoCompileData) {
        val player = trigger.dispatcher.get<Player>() ?: return

        val cost = element.config.getIntFromExpression("${type.id}_cost", trigger.data)

        player.magic[type] -= cost
    }
}
