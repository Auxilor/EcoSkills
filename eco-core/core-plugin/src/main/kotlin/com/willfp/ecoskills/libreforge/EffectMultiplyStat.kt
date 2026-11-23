package com.willfp.ecoskills.libreforge

import com.willfp.ecoskills.api.addStatModifier
import com.willfp.ecoskills.api.modifiers.ModifierOperation
import com.willfp.ecoskills.api.modifiers.StatModifier
import com.willfp.ecoskills.api.removeStatModifier
import com.willfp.ecoskills.stats.Stats
import org.bukkit.entity.Player
import wiki.ecohub.config.ArgType
import wiki.ecohub.config.Config
import wiki.ecohub.config.arguments
import wiki.ecohub.core.registry.NoCompileData
import wiki.ecohub.rules.effect.Effect
import wiki.ecohub.rules.effect.Identifiers
import wiki.ecohub.rules.effect.RunOrder
import wiki.ecohub.rules.holder.Dispatcher
import wiki.ecohub.rules.holder.ProvidedHolder
import wiki.ecohub.rules.holder.get

object EffectMultiplyStat : Effect<NoCompileData>("multiply_stat") {
    override val description = "Multiplies one of the player's stats by the given multiplier while the holder is active."

    override val categories = setOf("player")

    override val runOrder = RunOrder.START

    override val arguments = arguments {
        require(
            "stat",
            "You must specify the stat!",
            description = "The stat to multiply.",
            type = ArgType.STRING
        )
        require(
            "multiplier",
            "You must specify the multiplier!",
            description = "The multiplier to apply to the stat.",
            type = ArgType.EXPRESSION,
            example = "1 + %level% * 0.1"
        )
    }

    override val shouldReload = false

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        val player = dispatcher.get<Player>() ?: return
        val stat = Stats.getByID(config.getString("stat")) ?: return

        player.removeStatModifier(identifiers.uuid)
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
        dispatcher.get<Player>()?.removeStatModifier(identifiers.uuid)
    }
}