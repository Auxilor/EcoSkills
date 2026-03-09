package com.willfp.ecoskills.gui.components

import com.willfp.eco.core.gui.GUIComponent
import com.willfp.eco.core.gui.menu.MenuBuilder
import com.willfp.eco.core.gui.menu.MenuLayer

interface PositionedComponent : GUIComponent {
    val row: Int
    val column: Int

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
): MenuBuilder = if (component.isEnabled) addComponent(
    component.row,
    component.column,
    component
) else this

fun MenuBuilder.addComponent(
    layer: MenuLayer,
    component: PositionedComponent
): MenuBuilder = if (component.isEnabled) addComponent(
    layer,
    component.row,
    component.column,
    component
) else this
