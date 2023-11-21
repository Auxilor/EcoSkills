package com.willfp.ecoskills.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.api.addStatModifier
import com.willfp.ecoskills.api.modifiers.ModifierOperation
import com.willfp.ecoskills.api.modifiers.StatModifier
import com.willfp.ecoskills.api.removeStatModifier
import com.willfp.ecoskills.stats.Stats
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.effects.RunOrder
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object EffectMultiplyStat : Effect<NoCompileData>("multiply_stat") {
    override val runOrder = RunOrder.START

    override val arguments = arguments {
        require("stat", "You must specify the stat!")
        require("multiplier", "You must specify the multiplier!")
    }

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        val player = dispatcher.get<Player>() ?: return
        val stat = Stats.getByID(config.getString("stat")) ?: return

        player.addStatModifier(
            StatModifier(
                identifiers.uuid,
                stat,
                config.getDoubleFromExpression("multiplier", player),
                ModifierOperation.MULTIPLY
            )
        )
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        val player = dispatcher.get<Player>() ?: return

        player.removeStatModifier(identifiers.uuid)
    }
}