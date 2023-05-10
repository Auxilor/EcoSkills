@file:Suppress("UNCHECKED_CAST")

package com.willfp.ecoskills.stats

import com.willfp.ecoskills.api.getBaseStatLevel
import com.willfp.ecoskills.api.modifiers.ModifierOperation
import com.willfp.ecoskills.api.modifiers.StatModifier
import com.willfp.ecoskills.plugin
import org.bukkit.entity.Player
import java.util.UUID

private const val KEY_MODIFIERS = "ecoskills:stat_modifiers"

internal val Player.statModifiers: StatModifiers
    get() = StatModifiers(this)

class StatModifiers(
    private val player: Player
) {
    fun add(modifier: StatModifier) {
        if (!player.hasMetadata(KEY_MODIFIERS)) {
            player.setMetadata(KEY_MODIFIERS, plugin.createMetadataValue(mutableMapOf<UUID, StatModifier>()))
        }

        val modifiers = player.getMetadata(KEY_MODIFIERS).first().value() as MutableMap<UUID, StatModifier>

        modifiers[modifier.uuid] = modifier

        player.setMetadata(KEY_MODIFIERS, plugin.createMetadataValue(modifiers))
    }

    operator fun plusAssign(modifier: StatModifier) = add(modifier)

    fun remove(uuid: UUID): StatModifier? {
        if (!player.hasMetadata(KEY_MODIFIERS)) {
            return null
        }

        val modifiers = player.getMetadata(KEY_MODIFIERS).first().value() as MutableMap<UUID, StatModifier>

        val modifier = modifiers.remove(uuid)

        player.setMetadata(KEY_MODIFIERS, plugin.createMetadataValue(modifiers))

        return modifier
    }

    operator fun minusAssign(uuid: UUID) {
        remove(uuid)
    }

    operator fun minusAssign(modifier: StatModifier) {
        remove(modifier.uuid)
    }

    fun getModifiers(): List<StatModifier> {
        if (!player.hasMetadata(KEY_MODIFIERS)) {
            return emptyList()
        }

        val modifiers = player.getMetadata(KEY_MODIFIERS).first().value() as MutableMap<UUID, StatModifier>

        return modifiers.values.toList()
    }

    fun getModifiers(stat: Stat): List<StatModifier> {
        return getModifiers().filter { it.stat == stat }
    }

    fun getModifier(uuid: UUID): StatModifier? {
        return getModifiers().firstOrNull { it.uuid == uuid }
    }

    /**
     * Get how much a stat is modified by.
     */
    fun getBonusStatLevel(stat: Stat): Int {
        return getModifiedValue(stat) - player.getBaseStatLevel(stat)
    }

    /**
     * Get the modified value of a stat.
     */
    fun getModifiedValue(stat: Stat): Int {
        val modifiers = getModifiers(stat)

        var base = player.getBaseStatLevel(stat).toDouble()

        modifiers.filter { it.operation == ModifierOperation.ADD }.forEach {
            base += it.modifier
        }

        modifiers.filter { it.operation == ModifierOperation.MULTIPLY }.forEach {
            base *= it.modifier
        }

        return base.toInt()
    }
}
