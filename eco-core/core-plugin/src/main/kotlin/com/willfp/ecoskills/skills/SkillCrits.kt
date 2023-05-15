package com.willfp.ecoskills.skills

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
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

object SkillCritListener : Listener {
    @EventHandler(
        priority = EventPriority.HIGH
    )
    fun handle(event: EntityDamageByEntityEvent) {
        if (event.isSkillCrit) {
            event.damage *= event.skillCrit
        }
    }
}
