@file:Suppress("UNCHECKED_CAST")

package com.willfp.ecoskills.stats

import com.willfp.ecoskills.api.getBaseStatLevel
import com.willfp.ecoskills.api.modifiers.ModifierOperation
import com.willfp.ecoskills.api.modifiers.StatModifier
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

// Player UUID -> Stat Modifier UUID -> Stat Modifier
private val trackedStatModifiers = ConcurrentHashMap<UUID, MutableMap<UUID, StatModifier>>()

internal val Player.statModifiers: StatModifiers
    get() = StatModifiers(this)

class StatModifiers(
    private val player: Player
) {
    private val modifiers: MutableMap<UUID, StatModifier>
        get() = trackedStatModifiers.computeIfAbsent(player.uniqueId) { ConcurrentHashMap() }

    fun add(modifier: StatModifier) {
        modifiers[modifier.uuid] = modifier
    }

    operator fun plusAssign(modifier: StatModifier) = add(modifier)

    fun remove(uuid: UUID): StatModifier? {
        return modifiers.remove(uuid)
    }

    operator fun minusAssign(uuid: UUID) {
        remove(uuid)
    }

    operator fun minusAssign(modifier: StatModifier) {
        remove(modifier.uuid)
    }

    fun getModifiers(): List<StatModifier> {
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

        var level = player.getBaseStatLevel(stat).toDouble()

        modifiers.filter { it.operation == ModifierOperation.ADD }.forEach {
            level += it.modifier
        }

        modifiers.filter { it.operation == ModifierOperation.MULTIPLY }.forEach {
            level *= it.modifier
        }

        return level.toInt()
    }
}

// Little fixer-upper
object StatModifierListener : Listener {
    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        trackedStatModifiers.remove(event.player.uniqueId)
    }
}
