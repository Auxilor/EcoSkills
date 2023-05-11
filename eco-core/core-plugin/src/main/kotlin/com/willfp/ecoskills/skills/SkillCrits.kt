package com.willfp.ecoskills.skills

import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.util.WeakHashMap

private val map = WeakHashMap<EntityDamageByEntityEvent, Double>()

var EntityDamageByEntityEvent.skillCrit: Double
    get() = map[this] ?: 1.0
    set(value) {
        map[this] = value
    }

val EntityDamageByEntityEvent.isSkillCrit: Boolean
    get() = map.containsKey(this)
