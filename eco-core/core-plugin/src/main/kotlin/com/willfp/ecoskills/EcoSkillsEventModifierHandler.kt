package com.willfp.ecoskills

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

private val critMap = mutableMapOf<EntityDamageByEntityEvent, Boolean>()

class EcoSkillsEventModifierHandler(
    private val plugin: EcoSkillsPlugin
) : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun preventLeak(event: EntityDamageByEntityEvent) {
        plugin.scheduler.run {
            critMap.remove(event)
        }
    }
}

var EntityDamageByEntityEvent.isCrit: Boolean
    get() {
        return critMap[this] ?: false
    }
    set(isCrit) {
        critMap[this] = isCrit
    }