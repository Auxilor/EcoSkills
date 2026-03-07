package com.willfp.ecoskills.magic

import com.willfp.ecoskills.plugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object MagicHandler {
    internal fun startTicking() {
        // Stagger to avoid lag spikes with other plugins? Maybe?
        plugin.scheduler.runTimer(18, 20) {
            for (player in Bukkit.getOnlinePlayers()) {
                for (type in MagicTypes.values()) {
                    type.tick(player)
                }
            }
        }
    }
}

object MagicListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        plugin.scheduler.runLater(2) {
            for (type in MagicTypes.values()) {
                if (type.joinOnFull) {
                    event.player.magic[type] = type.getLimit(event.player)
                }
            }
        }
    }
}

internal val Player.magic: MagicMap
    get() = MagicMap(this)
