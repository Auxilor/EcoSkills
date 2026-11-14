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
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object EffectMultiplyAllStats : Effect<NoCompileData>("multiply_all_stats") {
    override val description = "Multiplies all of the player's stats by the given multiplier while the holder is active."

    override val categories = setOf("player")

    override val runOrder = RunOrder.START

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the multiplier!",
            description = "The multiplier to apply to every stat.",
            type = ArgType.EXPRESSION,
            example = "1 + %level% * 0.1"
        )
    }

    override val shouldReload = false

    private val activeModifiers = ConcurrentHashMap<UUID, MutableSet<UUID>>()

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        val player = dispatcher.get<Player>() ?: return
        val uuids = activeModifiers.computeIfAbsent(identifiers.uuid) { ConcurrentHashMap.newKeySet() }

        for (stat in Stats.values()) {
            val modifierUUID = UUID.nameUUIDFromBytes("${identifiers.uuid}_${stat.id}".toByteArray())
            uuids.add(modifierUUID)
            player.removeStatModifier(modifierUUID)
            player.addStatModifier(
                StatModifier(
                    modifierUUID,
                    stat,
                    config.getDoubleFromExpression("multiplier", player),
                    ModifierOperation.MULTIPLY
                )
            )
        }
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        val player = dispatcher.get<Player>() ?: return

        val modifierUUIDs = activeModifiers.remove(identifiers.uuid) ?: return

        for (uuid in modifierUUIDs) {
            player.removeStatModifier(uuid)
        }
    }
}
