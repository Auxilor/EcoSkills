package com.willfp.ecoskills.integrations.hologram.wrappers

import com.willfp.ecoskills.integrations.hologram.HologramWrapper
import me.gholo.api.GHoloAPI
import com.willfp.ecoskills.EcoSkillsPlugin
import org.bukkit.Location

class GHoloWrapper : HologramWrapper {
    var id = 0
    override fun spawnHolo(loc: Location, contents: List<String>, toRemove: Int) {
        val api = GHoloAPI()
        val res = id++
        api.insertHolo(res.toString(), loc, contents)
        EcoSkillsPlugin.getInstance().scheduler.runLater(
            { api.removeHolo(res.toString()) },
            30
        )
    }
}