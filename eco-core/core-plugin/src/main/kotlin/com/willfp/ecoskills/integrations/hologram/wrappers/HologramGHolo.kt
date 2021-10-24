package com.willfp.ecoskills.integrations.hologram.wrappers

import com.willfp.eco.core.EcoPlugin
import com.willfp.ecoskills.integrations.hologram.HologramWrapper
import me.gholo.api.GHoloAPI
import org.bukkit.Location
import java.util.*

class HologramGHolo(
    private val plugin: EcoPlugin
) : HologramWrapper {
    override fun spawnHolo(location: Location, contents: List<String>, lifespan: Int) {
        val api = GHoloAPI()
        val id = UUID.randomUUID().toString()

        api.insertHolo(id, location, contents)

        plugin.scheduler.runLater({
            api.removeHolo(id)
        }, lifespan.toLong())
    }
}