package com.willfp.ecoskills

import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.ecoskills.api.StatModifier
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.stats.Stat
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType

private val modifierKey: NamespacedKey = NamespacedKeyUtils.create("ecoskills", "modifiers")

fun ItemStack.addStatModifier(modifier: StatModifier) {
    val meta = this.itemMeta ?: return

    val container = meta.persistentDataContainer

    val context = container.adapterContext

    val modifiers = container.getOrDefault(modifierKey, PersistentDataType.TAG_CONTAINER, context.newPersistentDataContainer())

    modifiers.remove(modifier.key)
    modifiers.remove(modifier.slotKey)
    modifiers.set(modifier.key, PersistentDataType.INTEGER, modifier.amount)
    modifiers.set(modifier.slotKey, PersistentDataType.STRING, modifier.slots.map { slot -> slot.name }.toTypedArray().joinToString { "," })
}