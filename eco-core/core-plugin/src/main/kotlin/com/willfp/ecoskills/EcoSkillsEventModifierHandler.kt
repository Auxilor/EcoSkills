package com.willfp.ecoskills

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

object EcoSkillsEventModifierHandler: Listener {
    val critMap: MutableMap<EntityDamageByEntityEvent, Boolean> = HashMap()

    @EventHandler(priority = EventPriority.MONITOR)
    fun preventLeak(event: EntityDamageByEntityEvent) {
        EcoSkillsPlugin.getInstance().scheduler.runLater({
            critMap.remove(event)
        }, 1)
    }
}

var EntityDamageByEntityEvent.isCrit: Boolean
    get() {
        return EcoSkillsEventModifierHandler.critMap[this] ?: false
    }
    set(isCrit) {
        EcoSkillsEventModifierHandler.critMap[this] = isCrit
    }