package com.willfp.ecoskills.gui

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.fast.fast
import com.willfp.eco.core.gui.menu.MenuBuilder
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.gui.slot.SlotBuilder
import com.willfp.eco.core.items.Items

class PositionableSlot internal constructor(
    config: Config,
    private val builder: SlotBuilder.() -> Unit
) {
    private val row = config.getInt("position.row")
    private val column = config.getInt("position.column")

    private val enabled = config.getBoolOrNull("enabled") ?: true

    private val icon = Items.lookup(config.getString("icon"))
    private val customLore = config.getStringsOrNull("lore")

    fun addTo(builder: MenuBuilder) {
        if (enabled) {
            builder.addComponent(
                row,
                column,
                slot(icon.item.clone().apply {
                    if (customLore != null) {
                        this.fast().lore = customLore
                    }
                }) {
                    builder()
                }
            )
        }
    }
}

fun positionableSlot(config: Config, builder: SlotBuilder.() -> Unit): PositionableSlot {
    return PositionableSlot(config, builder)
}
