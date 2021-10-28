package com.willfp.ecoskills

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.eco.core.config.json.JSONTransientConfig
import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.ecoskills.api.modifier.ItemStatModifier
import com.willfp.ecoskills.api.modifier.PlayerStatModifier
import com.willfp.ecoskills.api.modifier.StatModifier
import com.willfp.ecoskills.stats.Stats
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

/*
Item Modifiers
 */

private val modifierKey: NamespacedKey = NamespacedKeyUtils.create("ecoskills", "modifiers")
private val statKey: NamespacedKey = NamespacedKeyUtils.create("ecoskills", "stat")
private val amountKey: NamespacedKey = NamespacedKeyUtils.create("ecoskills", "amount")
private val slotsKey: NamespacedKey = NamespacedKeyUtils.create("ecoskills", "slots")

private fun PersistentDataContainer.applyModifiers(tag: PersistentDataContainer) {
    this.set(modifierKey, PersistentDataType.TAG_CONTAINER, tag)
}

private fun getModifiersTag(meta: ItemMeta): PersistentDataContainer {
    val container = meta.persistentDataContainer
    val context = container.adapterContext
    return container.getOrDefault(modifierKey, PersistentDataType.TAG_CONTAINER, context.newPersistentDataContainer())
}

fun ItemStack.addStatModifier(modifier: ItemStatModifier) {
    val meta = this.itemMeta ?: return
    val modifiers = getModifiersTag(meta)

    modifiers.remove(modifier.key)

    val modifierTag = modifiers.adapterContext.newPersistentDataContainer()

    modifierTag.set(statKey, PersistentDataType.STRING, modifier.stat.id)
    modifierTag.set(amountKey, PersistentDataType.INTEGER, modifier.amount)
    modifierTag.set(
        slotsKey,
        PersistentDataType.STRING,
        modifier.slots.map { slot -> slot.name }.toTypedArray().joinToString { "," })

    modifiers.set(modifier.key, PersistentDataType.TAG_CONTAINER, modifierTag)

    meta.persistentDataContainer.applyModifiers(modifiers)

    this.itemMeta = meta
}

fun ItemStack.removeStatModifier(modifier: ItemStatModifier) {
    val meta = this.itemMeta ?: return
    val modifiers = getModifiersTag(meta)

    modifiers.remove(modifier.key)

    meta.persistentDataContainer.applyModifiers(modifiers)

    this.itemMeta = meta
}

fun ItemStack.getStatModifierKeys(): MutableSet<NamespacedKey> {
    val meta = this.itemMeta ?: return HashSet()
    val modifiers = getModifiersTag(meta)

    return modifiers.keys
}

fun ItemStack.getStatModifiers(): MutableSet<ItemStatModifier> {
    val keys = HashSet<ItemStatModifier>()
    for (modifier in this.getStatModifierKeys().stream().map { key -> this.getStatModifier(key) }) {
        if (modifier != null) {
            keys.add(modifier)
        }
    }
    return keys
}

fun ItemStack.getStatModifier(key: NamespacedKey): ItemStatModifier? {
    val meta = this.itemMeta ?: return null
    val modifiers = getModifiersTag(meta)

    return if (modifiers.has(key, PersistentDataType.TAG_CONTAINER)) {
        val modifierTag = modifiers.get(key, PersistentDataType.TAG_CONTAINER)!!

        val stat = Stats.getByID(modifierTag.get(statKey, PersistentDataType.STRING)!!)!!
        val amount = modifierTag.get(amountKey, PersistentDataType.INTEGER)!!
        val slots = modifierTag.get(slotsKey, PersistentDataType.STRING)!!.split(",")
            .map { s -> EquipmentSlot.valueOf(s) }
            .toCollection(ArrayList())

        ItemStatModifier(key, stat, amount, *slots.toTypedArray())
    } else {
        null
    }
}

/*
Player Modifiers
 */

private const val META_MODIFIERS = "ecoskills_modifiers"
private const val META_STAT_KEY = "stat"
private const val META_AMOUNT_KEY = "amount"

private fun Player.applyModifiers(meta: JSONConfig) {
    this.setMetadata(META_MODIFIERS, plugin.metadataValueFactory.create(meta))
}

@Suppress("UNCHECKED_CAST")
private fun getModifiersTag(player: Player): JSONConfig {
    return player.getMetadata(META_MODIFIERS).getOrNull(0)?.value() as JSONConfig? ?: JSONTransientConfig(emptyMap())
}

fun Player.addStatModifier(modifier: StatModifier) {
    val modifiers = getModifiersTag(this)

    modifiers.set(
        modifier.key.toString(), JSONTransientConfig(
            mapOf(
                Pair(META_STAT_KEY, modifier.stat.id),
                Pair(META_AMOUNT_KEY, modifier.amount)
            )
        )
    )

    this.applyModifiers(modifiers)

    for (stat in Stats.values()) {
        stat.updateStatLevel(this)
    }
}

fun Player.removeStatModifier(modifier: StatModifier) {
    val modifiers = getModifiersTag(this)

    modifiers.set(modifier.key.toString(), null)

    this.applyModifiers(modifiers)

    for (stat in Stats.values()) {
        stat.updateStatLevel(this)
    }
}

fun Player.getStatModifierKeys(): MutableSet<NamespacedKey> {
    val modifiers = getModifiersTag(this)
    return modifiers.getKeys(false).mapNotNull { NamespacedKey.fromString(it) }.toMutableSet()
}

fun Player.getStatModifiers(): MutableSet<PlayerStatModifier> {
    val keys = HashSet<PlayerStatModifier>()
    for (modifier in this.getStatModifierKeys().stream().map { key -> this.getStatModifier(key) }) {
        if (modifier != null) {
            keys.add(modifier)
        }
    }
    return keys
}

fun Player.getStatModifier(key: NamespacedKey): PlayerStatModifier? {
    val modifiers = getModifiersTag(this)

    return if (modifiers.has(key.toString())) {
        val modifier = modifiers.getSubsection(key.toString())

        val stat = Stats.getByID(modifier.getString(META_STAT_KEY))!!
        val amount = modifier.getInt(META_AMOUNT_KEY)

        PlayerStatModifier(key, stat, amount)
    } else {
        null
    }
}