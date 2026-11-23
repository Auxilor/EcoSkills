package com.willfp.ecoskills.libreforge

import com.willfp.ecoskills.magic.MagicType
import com.willfp.ecoskills.magic.magic
import org.bukkit.entity.Player
import wiki.ecohub.config.ConfigurableElement
import wiki.ecohub.core.registry.NoCompileData
import wiki.ecohub.rules.argument.EffectArgument
import wiki.ecohub.rules.holder.get
import wiki.ecohub.rules.trigger.DispatchedTrigger

class EffectArgumentMagicCost(private val type: MagicType) : EffectArgument<NoCompileData>("${type.id}_cost") {
    override fun isMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: NoCompileData): Boolean {
        val player = trigger.dispatcher.get<Player>() ?: return false

        val cost = element.config.getIntFromExpression("${type.id}_cost", player)

        return player.magic[type] >= cost
    }

    override fun ifMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: NoCompileData) {
        val player = trigger.dispatcher.get<Player>() ?: return

        val cost = element.config.getIntFromExpression("${type.id}_cost", player)

        player.magic[type] -= cost
    }
}
