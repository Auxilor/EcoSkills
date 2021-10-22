package com.willfp.ecoskills.integrations.hologram

import org.bukkit.Location

interface HologramWrapper {
    fun spawnHolo(loc: Location, contents: List<String>, toRemove: Int)
}