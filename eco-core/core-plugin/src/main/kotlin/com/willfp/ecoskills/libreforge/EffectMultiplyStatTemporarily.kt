package com.willfp.ecoskills.libreforge

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.api.addStatModifier
import com.willfp.ecoskills.api.modifiers.ModifierOperation
import com.willfp.ecoskills.api.modifiers.StatModifier
import com.willfp.ecoskills.api.removeStatModifier
import com.willfp.ecoskills.stats.Stats
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import java.util.UUID

class EffectMultiplyStatTemporarily(
    private val plugin: EcoPlugin
) : Effect<NoCompileData>("add_stat_temporarily") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("stat", "You must specify the stat!")
        require("amount", "You must specify the amount to add/remove!")
        require("duration", "You must specify the duration for the boost!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val stat = Stats.getByID(config.getString("stat")) ?: return false
        val multiplier = config.getDoubleFromExpression("multiplier", data)
        val uuid = UUID.nameUUIDFromBytes("mst_${NumberUtils.randInt(0, 1000000)}".toByteArray())

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
