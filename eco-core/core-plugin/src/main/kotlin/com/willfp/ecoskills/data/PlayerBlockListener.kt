package com.willfp.ecoskills.data

import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.plugin
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockMultiPlaceEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.persistence.PersistentDataType

class PlayerBlockListener(
    private val plugin: EcoSkillsPlugin
) : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlace(event: BlockPlaceEvent) {
        val block = event.blockPlaced

        if (block.type.toString().uppercase() == "BREWING_STAND") {
            block.setBrewingStandOwner(event.player)
            return
        }

        writeKey(block)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlace(event: BlockMultiPlaceEvent) {
        val block = event.blockPlaced

        if (block.type.toString().uppercase() == "BREWING_STAND") {
            block.setBrewingStandOwner(event.player)
            return
        }

        writeKey(block)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBreak(event: BlockBreakEvent) {
        val block = event.block

        if (block.type.toString().uppercase() == "BREWING_STAND") {
            block.removeBrewingStand()
            return
        }

        this.plugin.scheduler.runLater({
            removeKey(block)
        }, 1)
    }

    private fun writeKey(block: Block) {
        val loc = block.location.hashCode().toString(16)
        block.chunk.persistentDataContainer.set(NamespacedKeyUtils.create("ecoskills", loc.lowercase()), PersistentDataType.INTEGER, 1)
    }

    private fun removeKey(block: Block) {
        val loc = block.location.hashCode().toString(16)
        block.chunk.persistentDataContainer.remove(NamespacedKeyUtils.create("ecoskills", loc.lowercase()))
    }
}

fun Block.isPlayerPlaced(): Boolean {
    val chunk = this.chunk
    return chunk.persistentDataContainer.has(NamespacedKeyUtils.create("ecoskills", this.location.hashCode().toString(16)), PersistentDataType.INTEGER)
}

fun Block.setBrewingStandOwner(player: Player) {
    plugin.dataYml.set("brewing_stands.${this.location.blockX}:${this.location.blockY}:${this.location.blockZ}", player.uniqueId)
}

fun Block.getBrewingStandOwner(): String? {
    return plugin.dataYml.getStringOrNull("brewing_stands.${this.location.blockX}:${this.location.blockY}:${this.location.blockZ}")
}

fun Block.removeBrewingStand() {
    plugin.dataYml.set("brewing_stands.${this.location.blockX}:${this.location.blockY}:${this.location.blockZ}", null)
}