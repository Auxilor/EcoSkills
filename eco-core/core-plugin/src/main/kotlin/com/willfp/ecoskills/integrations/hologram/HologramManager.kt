package com.willfp.ecoskills.integrations.hologram

import org.bukkit.Location

object HologramManager {
    private val hologramWrappers = mutableListOf<HologramWrapper>()

    @JvmStatic
    fun register(wrapper: HologramWrapper) {
        hologramWrappers.add(wrapper)
    }

    @JvmStatic
    fun spawnHolo(location: Location, contents: List<String>, lifespan: Int) {
        hologramWrappers.firstOrNull()?.spawnHolo(location, contents, lifespan)
    }
}