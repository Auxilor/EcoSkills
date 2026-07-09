package com.willfp.ecoskills.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.api.addStatModifier
import com.willfp.ecoskills.api.modifiers.ModifierOperation
import com.willfp.ecoskills.api.modifiers.StatModifier
import com.willfp.ecoskills.api.removeStatModifier
import com.willfp.ecoskills.stats.Stats
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.effects.RunOrder
import com.willfp.libreforge.get
import org.bukkit.entity.Player
import java.util.UUID

object EffectMultiplyAllStats : Effect<NoCompileData>("multiply_all_stats") {
    override val description = "Multiplies all of the player's stats by the given multiplier while the holder is active."

    override val categories = setOf("player")

    override val runOrder = RunOrder.START

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the multiplier!",
            description = "The multiplier to apply to every stat.",
            type = ArgType.EXPRESSION
        )
    }

    override val shouldReload = false

    private val activeModifiers = HashMap<String, MutableSet<UUID>>()

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        val player = dispatcher.get<Player>() ?: return
        val lookupKey = holder.lookupKey(player)
        val uuids = activeModifiers.getOrPut(lookupKey) { mutableSetOf() }

        for (stat in Stats.values()) {
            val modifierUUID = UUID.nameUUIDFromBytes("${lookupKey}_${stat.id}".toByteArray())
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

        val lookupKey = holder.lookupKey(player)
        val modifierUUIDs = activeModifiers.remove(lookupKey) ?: return

        for (uuid in modifierUUIDs) {
            player.removeStatModifier(uuid)
        }
    }
}
