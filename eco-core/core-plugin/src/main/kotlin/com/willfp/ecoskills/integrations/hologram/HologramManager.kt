package com.willfp.ecoskills.integrations.hologram

import org.bukkit.Location

class HologramManager {

    companion object {

        private var hologramWrappers: MutableList<HologramWrapper> = ArrayList()

        fun register(wrapper: HologramWrapper) {
            hologramWrappers.add(wrapper)
        }

        fun spawnHolo(loc: Location, contents: List<String>, removeAfter: Int) {
            if (hologramWrappers.isEmpty()) return
            hologramWrappers[0].spawnHolo(loc, contents, removeAfter)
        }
    }

}