package com.willfp.ecoskills.gui.components

import com.willfp.eco.core.gui.GUIComponent
import com.willfp.eco.core.gui.SlotLocation
import com.willfp.eco.core.gui.menu.MenuBuilder

interface PositionedComponent : GUIComponent {
    val row: Int
    val column: Int

    val locations: List<SlotLocation>
        get() = listOf(SlotLocation(row, column))

    val isEnabled: Boolean
        get() = true

    val rowSize: Int
        get() = 1
    val columnSize: Int
        get() = 1

    override fun getRows() = rowSize
    override fun getColumns() = columnSize
}

fun MenuBuilder.addComponent(
    component: PositionedComponent
): MenuBuilder {
    if (component.isEnabled) {
        for (location in component.locations) {
            addComponent(location.row, location.column, component)
        }
    }

    return this
}

fun MenuBuilder.addComponent(
    layer: Int,
    component: PositionedComponent
): MenuBuilder {
    if (component.isEnabled) {
        for (location in component.locations) {
            addComponent(layer, location.row, location.column, component)
        }
    }

    return this
}
