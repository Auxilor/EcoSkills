package com.willfp.ecoskills.gui.components

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.ItemStackBuilder
import org.bukkit.entity.Player

class BackButton(
    config: Config,
    goBack: (Player) -> Unit
) : PositionedComponent {
    private val slot = slot(
        ItemStackBuilder(Items.lookup(config.getString("item")))
            .setDisplayName(config.getFormattedString("name"))
            .build()
    ) {
        onLeftClick { event, _ -> goBack(event.whoClicked as Player) }
    }

    override val row: Int = config.getInt("location.row")
    override val column: Int = config.getInt("location.column")

    override fun getSlotAt(row: Int, column: Int) = slot
}
