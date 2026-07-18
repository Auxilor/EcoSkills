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

object EffectAddStat : Effect<NoCompileData>("add_stat") {
    override val description = "Adds a flat amount to one of the player's stats while the holder is active."

    override val categories = setOf("player")

    override val runOrder = RunOrder.START

    override val arguments = arguments {
        require(
            "stat",
            "You must specify the stat!",
            description = "The stat to modify.",
            type = ArgType.STRING
        )
        require(
            "amount",
            "You must specify the amount to add/remove!",
            description = "The amount to add to the stat. Use a negative value to remove.",
            type = ArgType.EXPRESSION
        )
    }

    private val activeModifiers = HashMap<String, MutableSet<UUID>>()

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        val player = dispatcher.get<Player>() ?: return
        val stat = Stats.getByID(config.getString("stat")) ?: return

        val lookupKey = holder.lookupKey(player)
        val modifierUUID = UUID.nameUUIDFromBytes("${lookupKey}_${stat.id}".toByteArray())

        activeModifiers.getOrPut(lookupKey) { mutableSetOf() }.add(modifierUUID)

        player.removeStatModifier(modifierUUID)
        player.addStatModifier(
            StatModifier(
                modifierUUID,
                stat,
                config.getDoubleFromExpression("amount", player),
                ModifierOperation.ADD
            )
        )
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
