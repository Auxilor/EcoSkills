package com.willfp.ecoskills.integrations.hologram.wrappers

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI
import com.willfp.eco.core.EcoPlugin
import com.willfp.ecoskills.integrations.hologram.HologramWrapper
import org.bukkit.Location

class HologramHolographicDisplays(
    private val plugin: EcoPlugin
) : HologramWrapper {
    override fun spawnHolo(location: Location, contents: List<String>, lifespan: Int) {
        val hologram = HologramsAPI.createHologram(plugin, location)

        for (s in contents) {
            hologram.appendTextLine(s)
        }

        plugin.scheduler.runLater({
            hologram.delete()
        }, lifespan.toLong())
    }
}