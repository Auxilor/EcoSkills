package com.willfp.ecoskills.stats.modifier

import com.willfp.eco.core.events.ArmorChangeEvent
import com.willfp.ecoskills.addStatModifier
import com.willfp.ecoskills.getStatModifiers
import com.willfp.ecoskills.removeStatModifier
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.EquipmentSlot

class StatModifierListener : Listener {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onHold(event: PlayerItemHeldEvent) {
        val player = event.player

        val old = player.inventory.getItem(event.previousSlot)
        val new = player.inventory.getItem(event.newSlot)

        val oldMods = old?.getStatModifiers() ?: HashSet()
        val newMods = new?.getStatModifiers() ?: HashSet()

        for (oldMod in oldMods) {
            player.removeStatModifier(oldMod.key)
        }
        for (newMod in newMods) {
            if (newMod.slots.contains(EquipmentSlot.HAND)) {
                player.addStatModifier(newMod)
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onHold(event: PlayerSwapHandItemsEvent) {
        val player = event.player

        val offhandMods = event.offHandItem?.getStatModifiers() ?: HashSet()
        val mainhandMods = event.mainHandItem?.getStatModifiers() ?: HashSet()

        for (offhandMod in offhandMods) {
            player.removeStatModifier(offhandMod.key)
        }
        for (mainhandMod in mainhandMods) {
            player.removeStatModifier(mainhandMod.key)
        }

        for (mod in offhandMods) {
            if (mod.slots.contains(EquipmentSlot.OFF_HAND)) {
                player.addStatModifier(mod)
            }
        }

        for (mod in mainhandMods) {
            if (mod.slots.contains(EquipmentSlot.HAND)) {
                player.addStatModifier(mod)
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onHold(event: ArmorChangeEvent) {
        for (itemStack in event.before) {
            if (itemStack == null) {
                continue
            }
            val mods = itemStack.getStatModifiers()
            for (mod in mods) {
                event.player.removeStatModifier(mod.key)
            }
        }
        for (itemStack in event.after) {
            if (itemStack == null) {
                continue
            }
            val mods = itemStack.getStatModifiers()
            for (mod in mods) {
                when (itemStack) {
                    event.player.inventory.helmet -> {
                        if (mod.slots.contains(EquipmentSlot.HEAD)) {
                            event.player.addStatModifier(mod)
                        }
                    }
                    event.player.inventory.chestplate -> {
                        if (mod.slots.contains(EquipmentSlot.CHEST)) {
                            event.player.addStatModifier(mod)
                        }
                    }
                    event.player.inventory.leggings -> {
                        if (mod.slots.contains(EquipmentSlot.LEGS)) {
                            event.player.addStatModifier(mod)
                        }
                    }
                    event.player.inventory.boots -> {
                        if (mod.slots.contains(EquipmentSlot.FEET)) {
                            event.player.addStatModifier(mod)
                        }
                    }
                }
            }
        }
    }
}