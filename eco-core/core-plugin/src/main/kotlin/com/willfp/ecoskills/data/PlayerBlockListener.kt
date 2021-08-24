package com.willfp.ecoskills.data

import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.ecoskills.EcoSkillsPlugin
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockMultiPlaceEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.persistence.PersistentDataType

class PlayerBlockListener(
    private val plugin: EcoSkillsPlugin
) : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlace(event: BlockPlaceEvent) {
        val block = event.blockPlaced

        writeKey(block)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlace(event: BlockMultiPlaceEvent) {
        val block = event.blockPlaced

        writeKey(block)
    }

    private fun writeKey(block: Block) {
        val loc = block.location.hashCode().toString(16)
        block.chunk.persistentDataContainer.set(NamespacedKeyUtils.create("ecoskills", loc.lowercase()), PersistentDataType.INTEGER, 1)
    }
}

fun Block.isPlayerPlaced(): Boolean {
    val chunk = this.chunk
    return chunk.persistentDataContainer.has(NamespacedKeyUtils.create("ecoskills", this.location.hashCode().toString(16)), PersistentDataType.INTEGER)
}

fun Block.removeEcoPlacedMetadata() {
    val chunk = this.chunk
    chunk.persistentDataContainer.remove(NamespacedKeyUtils.create("ecoskills", this.location.hashCode().toString(16)))
}