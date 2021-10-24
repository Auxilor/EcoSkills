package com.willfp.ecoskills.integrations.hologram

import org.bukkit.Location

interface HologramWrapper {
    fun spawnHolo(location: Location, contents: List<String>, lifespan: Int)
}