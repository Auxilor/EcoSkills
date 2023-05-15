package com.willfp.ecoskills.mana

import com.willfp.eco.core.EcoPlugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class MagicHandler(private val plugin: EcoPlugin) {
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

internal val Player.magic: MagicMap
    get() = MagicMap(this)
