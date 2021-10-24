package com.willfp.ecoskills.integrations.hologram.wrappers

import com.Zrips.CMI.CMI
import com.willfp.ecoskills.integrations.hologram.HologramWrapper
import com.Zrips.CMI.Modules.Holograms.CMIHologram
import com.Zrips.CMI.Modules.Holograms.HologramManager
import java.util.UUID
import com.willfp.ecoskills.EcoSkillsPlugin
import net.Zrips.CMILib.Container.CMILocation
import org.bukkit.Location

class CMIWrapper : HologramWrapper {
    override fun spawnHolo(loc: Location, contents: List<String>, toRemove: Int) {
        val holo = CMIHologram(UUID.randomUUID().toString(), CMILocation(loc))
        holo.enable()
        holo.lines = contents
        CMI.getInstance().hologramManager.addHologram(holo);
        EcoSkillsPlugin.getInstance().scheduler.runLater(
            { CMI.getInstance().hologramManager.removeHolo(holo); },
            30
        )
    }
}