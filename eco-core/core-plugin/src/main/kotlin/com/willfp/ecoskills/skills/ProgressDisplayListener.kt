package com.willfp.ecoskills.skills

import com.willfp.eco.core.EcoPlugin
import com.willfp.ecoskills.api.PlayerSkillExpGainEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class ProgressDisplayListener(
    private val plugin: EcoPlugin
) : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onProgress(event: PlayerSkillExpGainEvent) {

    }
}