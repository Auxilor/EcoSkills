package com.willfp.ecoskills

import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.util.WeakHashMap

private val critMap = WeakHashMap<EntityDamageByEntityEvent, Boolean>()

var EntityDamageByEntityEvent.isCrit: Boolean
    get() {
        return critMap[this] ?: false
    }
    set(isCrit) {
        critMap[this] = isCrit
    }