package com.willfp.ecoskills.magic

import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.runOwned
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object MagicHandler {
    internal fun startTicking() {
        // Stagger to avoid lag spikes with other plugins? Maybe?
        plugin.scheduler.global().runTimer(18, 20) {
            for (player in Bukkit.getOnlinePlayers()) {
                player.runOwned {
                    if (player.isOnline) {
                        for (type in MagicTypes.values()) {
                            type.tick(player)
                        }
                    }
                }
            }
        }
    }
}

object MagicListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        plugin.scheduler.on(event.player).runLater(2) {
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
