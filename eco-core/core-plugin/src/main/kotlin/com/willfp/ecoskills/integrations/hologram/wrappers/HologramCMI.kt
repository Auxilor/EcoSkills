package com.willfp.ecoskills.integrations.hologram.wrappers

import com.Zrips.CMI.CMI
import com.Zrips.CMI.Modules.Holograms.CMIHologram
import com.willfp.eco.core.EcoPlugin
import com.willfp.ecoskills.integrations.hologram.HologramWrapper
import net.Zrips.CMILib.Container.CMILocation
import org.bukkit.Location
import java.util.*

class HologramCMI(
    private val plugin: EcoPlugin
) : HologramWrapper {
    override fun spawnHolo(location: Location, contents: List<String>, lifespan: Int) {
        val holo = CMIHologram(UUID.randomUUID().toString(), CMILocation(location))
        holo.enable()
        holo.lines = contents
        CMI.getInstance().hologramManager.addHologram(holo)
        plugin.scheduler.runLater({
            CMI.getInstance().hologramManager.removeHolo(holo)
        }, lifespan.toLong())
    }
}