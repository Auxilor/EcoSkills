package com.willfp.ecoskills.libreforge

import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.slot.SlotItemProvidedHolder
import org.bukkit.entity.Player

/**
 * A key uniquely identifying this holder instance for a player, used to key stat
 * modifier UUIDs. Must include the equipment slot (when known) so that the same
 * holder config (e.g. the same enchantment at the same level) applied via multiple
 * armor pieces is tracked as separate stacking instances rather than colliding.
 */
fun ProvidedHolder.lookupKey(player: Player): String {
    val slotId = (this as? SlotItemProvidedHolder<*>)?.slotType?.id ?: "global"
    return "${player.uniqueId}_${holder.id}_$slotId"
}
