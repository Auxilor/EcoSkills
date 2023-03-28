package com.willfp.ecoskills.libreforge

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.api.EcoSkillsAPI
import com.willfp.ecoskills.api.modifier.ModifierOperation
import com.willfp.ecoskills.api.modifier.PlayerStatModifier
import com.willfp.ecoskills.stats.Stats
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

class EffectMultiplyStatTemporarily(
    private val plugin: EcoPlugin
) : Effect<NoCompileData>("multiply_stat_temporarily") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("stat", "You must specify the stat!")
        require("multiplier", "You must specify the multiplier!")
        require("duration", "You must specify the duration for the boost!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val stat = Stats.getByID(config.getString("stat")) ?: return false
        val amount = config.getDoubleFromExpression("multiplier", data)
        val key = plugin.namespacedKeyFactory.create("mst_${NumberUtils.randInt(0, 1000000)}")

        EcoSkillsAPI.getInstance().addStatModifier(
            player,
            PlayerStatModifier(key, stat, amount, ModifierOperation.MULTIPLY)
        )

        plugin.scheduler.runLater(config.getIntFromExpression("duration", data).toLong()) {
            EcoSkillsAPI.getInstance().removeStatModifier(player, key)
        }

        return true
    }
}
