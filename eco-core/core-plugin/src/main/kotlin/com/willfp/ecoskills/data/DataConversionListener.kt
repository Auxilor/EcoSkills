package com.willfp.ecoskills.data

import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.convertPersistentToYml
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.net.http.WebSocket

class DataConversionListener(
    private val plugin: EcoSkillsPlugin
): Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.player.convertPersistentToYml()
    }
}