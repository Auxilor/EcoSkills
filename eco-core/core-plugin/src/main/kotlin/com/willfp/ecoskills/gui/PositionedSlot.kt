package com.willfp.ecoskills.gui

import com.willfp.eco.core.gui.menu.MenuBuilder
import com.willfp.eco.core.gui.slot.Slot

class PositionedSlot(
    val enabled: Boolean,
    val row: Int,
    val column: Int,
    val slot: Slot
) {
    fun add(builder: MenuBuilder) {
        if (enabled) {
            builder.addComponent(row, column, slot)
        }
    }
}
