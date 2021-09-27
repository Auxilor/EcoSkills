package com.willfp.ecoskills.data

import com.willfp.eco.core.EcoPlugin
import com.willfp.ecoskills.profile
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
            profile.write("name", this.displayName)
        }

        return profile.readString("name", this.name ?: "Unknown Player")
    }
    set(value) {
        return profile.write("name", value)
    }
