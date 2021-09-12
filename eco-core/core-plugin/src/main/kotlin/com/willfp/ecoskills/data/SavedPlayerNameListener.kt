package com.willfp.ecoskills.data

import com.willfp.eco.core.EcoPlugin
import com.willfp.ecoskills.plugin
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class SavedPlayerNameListener(
    private val plugin: EcoPlugin
) : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.player.savedDisplayName = event.player.displayName
    }

    @EventHandler
    fun onJoin(event: PlayerQuitEvent) {
        event.player.savedDisplayName = event.player.displayName
    }
}

var OfflinePlayer.savedDisplayName: String
    get() {
        if (this is Player) {
            plugin.dataYml.set("player.${this.uniqueId}.name", this.displayName)
        }

        return plugin.dataYml.getStringOrNull("player.${this.uniqueId}.name") ?: this.name ?: "Unknown Player"
    }
    set(value) {
        plugin.dataYml.set("player.${this.uniqueId}.name", value)
    }
