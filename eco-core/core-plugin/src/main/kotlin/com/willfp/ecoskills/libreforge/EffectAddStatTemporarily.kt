package com.willfp.ecoskills.libreforge

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.api.EcoSkillsAPI
import com.willfp.ecoskills.api.modifier.PlayerStatModifier
import com.willfp.ecoskills.stats.Stats
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

class EffectAddStatTemporarily(
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
        val amount = config.getIntFromExpression("amount", data)
        val key = plugin.namespacedKeyFactory.create("ast_${NumberUtils.randInt(0, 1000000)}")

        EcoSkillsAPI.getInstance().addStatModifier(
            player,
            PlayerStatModifier(key, stat, amount)
        )

        plugin.scheduler.runLater(config.getIntFromExpression("duration", data).toLong()) {
            EcoSkillsAPI.getInstance().removeStatModifier(player, key)
        }

        return true
    }
}
