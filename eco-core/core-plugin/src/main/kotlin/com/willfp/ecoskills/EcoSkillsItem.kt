package com.willfp.ecoskills

import com.google.common.collect.ImmutableSet
import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.ecoskills.api.StatModifier
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.stats.Stat
import com.willfp.ecoskills.stats.Stats
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType

private val modifierKey: NamespacedKey = NamespacedKeyUtils.create("ecoskills", "modifiers")
private val statKey: NamespacedKey = NamespacedKeyUtils.create("ecoskills", "stat")
private val amountKey: NamespacedKey = NamespacedKeyUtils.create("ecoskills", "amount")
private val slotsKey: NamespacedKey = NamespacedKeyUtils.create("ecoskills", "slots")

private fun getModifiersTag(meta: ItemMeta): PersistentDataContainer {
    val container = meta.persistentDataContainer
    val context = container.adapterContext
    return container.getOrDefault(modifierKey, PersistentDataType.TAG_CONTAINER, context.newPersistentDataContainer())
}

fun ItemStack.addStatModifier(modifier: StatModifier) {
    val meta = this.itemMeta ?: return
    val modifiers = getModifiersTag(meta)

    modifiers.remove(modifier.key)

    val modifierTag = modifiers.adapterContext.newPersistentDataContainer()

    modifierTag.set(statKey, PersistentDataType.STRING, modifier.stat.id)
    modifierTag.set(amountKey, PersistentDataType.INTEGER, modifier.amount)
    modifierTag.set(slotsKey, PersistentDataType.STRING, modifier.slots.map { slot -> slot.name }.toTypedArray().joinToString { "," })

    modifiers.set(modifier.key, PersistentDataType.TAG_CONTAINER, modifierTag)

    this.itemMeta = meta
}

fun ItemStack.removeStatModifier(modifier: StatModifier) {
    val meta = this.itemMeta ?: return
    val modifiers = getModifiersTag(meta)

    modifiers.remove(modifier.key)

    this.itemMeta = meta
}

fun ItemStack.getStatModifierKeys(): MutableSet<NamespacedKey> {
    val meta = this.itemMeta ?: return HashSet()
    val modifiers = getModifiersTag(meta)

    return modifiers.keys
}

fun ItemStack.getStatModifiers(): MutableSet<StatModifier> {
    val keys = HashSet<StatModifier>()
    for (modifier in this.getStatModifierKeys().stream().map { key -> this.getStatModifier(key) }) {
        if (modifier != null) {
            keys.add(modifier)
        }
    }
    return keys
}

fun ItemStack.getStatModifier(key: NamespacedKey): StatModifier? {
    val meta = this.itemMeta ?: return null
    val modifiers = getModifiersTag(meta)

    return if (modifiers.has(key, PersistentDataType.TAG_CONTAINER)) {
        val modifierTag = modifiers.get(key, PersistentDataType.TAG_CONTAINER)!!

        val stat = Stats.getByID(modifierTag.get(statKey, PersistentDataType.STRING)!!)!!
        val amount = modifierTag.get(amountKey, PersistentDataType.INTEGER)!!
        val slots = modifierTag.get(slotsKey, PersistentDataType.STRING)!!.split(",")
            .map { s -> EquipmentSlot.valueOf(s) }
            .toCollection(ArrayList())

        StatModifier(key, stat, amount, *slots.toTypedArray())
    } else {
        null
    }
}