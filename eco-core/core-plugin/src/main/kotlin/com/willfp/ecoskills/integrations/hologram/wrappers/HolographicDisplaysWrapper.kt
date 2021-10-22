package com.willfp.ecoskills.integrations.hologram.wrappers

import com.willfp.ecoskills.integrations.hologram.HologramWrapper
import com.gmail.filoghost.holographicdisplays.api.Hologram
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI
import com.willfp.ecoskills.EcoSkillsPlugin
import org.bukkit.Location

class HolographicDisplaysWrapper : HologramWrapper {
    override fun spawnHolo(loc: Location, contents: List<String>, toRemove: Int) {
        val hologram = HologramsAPI.createHologram(EcoSkillsPlugin.getInstance(), loc)
        for (s in contents) {
            hologram.appendTextLine(s)
        }
        EcoSkillsPlugin.getInstance().scheduler.runLater({ hologram.delete() }, 30)
    }
}