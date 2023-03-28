package com.willfp.ecoskills.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.api.EcoSkillsAPI
import com.willfp.ecoskills.api.modifier.PlayerStatModifier
import com.willfp.ecoskills.stats.Stats
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import org.bukkit.entity.Player

object EffectAddStat : Effect<NoCompileData>("add_stat") {
    override val arguments = arguments {
        require("stat", "You must specify the stat!")
        require("amount", "You must specify the amount to add/remove!")
    }

    override fun onEnable(player: Player, config: Config, identifiers: Identifiers, holder: ProvidedHolder, compileData: NoCompileData) {
        EcoSkillsAPI.getInstance().addStatModifier(
            player,
            PlayerStatModifier(
                identifiers.key,
                Stats.getByID(config.getString("stat")) ?: return,
                config.getIntFromExpression("amount", player)
            )
        )
    }

    override fun onDisable(player: Player, identifiers: Identifiers, holder: ProvidedHolder) {
        EcoSkillsAPI.getInstance().removeStatModifier(
            player,
            identifiers.key
        )
    }
}
