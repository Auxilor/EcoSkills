package com.willfp.ecoskills.gui

import com.willfp.eco.core.config.updating.ConfigUpdater
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot.FillerMask
import com.willfp.eco.core.gui.slot.MaskMaterials
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.ItemStackBuilder
import com.willfp.eco.core.items.builder.SkullBuilder
import com.willfp.eco.util.StringUtils
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.skills.Skills
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.SkullMeta
import java.util.Locale
import java.util.function.Function

object SkillGUI {
    @JvmStatic
    lateinit var homeMenu: Menu

    @JvmStatic
    @ConfigUpdater
    fun update(plugin: EcoSkillsPlugin) {
        homeMenu = buildHomeMenu(plugin)
    }

    @JvmStatic
    private fun buildHomeMenu(plugin: EcoSkillsPlugin): Menu {
        val maskPattern = plugin.configYml.getStrings("gui.mask.pattern", false).toTypedArray()
        val maskMaterials = plugin.configYml
            .getStrings("gui.mask.materials", false)
            .mapNotNull { Material.getMaterial(it.uppercase(Locale.getDefault())) }
            .toTypedArray()
        val closeItem = Items.lookup(plugin.configYml.getString("gui.close.material", false)).item
        val playerHeadItemBuilder = Function { player: Player ->
            val itemStack = SkullBuilder()
                .setDisplayName(
                    StringUtils.format(
                        plugin.configYml.getString("gui.player-info.name", false)
                            .replace("%player%", player.displayName), player
                    )
                )
                .addLoreLines {
                    val lore: MutableList<String> = ArrayList()
                    for (string in plugin.configYml.getStrings("gui.player-info.lore", false)) {
                        lore.add(StringUtils.format(string!!, player))
                    }
                    lore
                }
                .build()
            val meta = itemStack.itemMeta as SkullMeta
            meta.owningPlayer = player
            itemStack.itemMeta = meta
            itemStack
        }
        return Menu.builder(plugin.configYml.getInt("gui.rows"))
            .setTitle(plugin.langYml.getString("menu.title"))
            .setMask(
                FillerMask(
                    MaskMaterials(
                        *maskMaterials
                    ),
                    *maskPattern
                )
            )
            .setSlot(
                plugin.configYml.getInt("gui.player-info.row"),
                plugin.configYml.getInt("gui.player-info.column"),
                Slot.builder(playerHeadItemBuilder).build()
            )
            .modfiy { menuBuilder ->
                for (skill in Skills.values()) {
                    menuBuilder.setSlot(
                        skill.config.getInt("gui.position.row"),
                        skill.config.getInt("gui.position.column"),
                        skill.gui.slot
                    )
                }
            }
            .setSlot(plugin.configYml.getInt("gui.close.location.row"),
                plugin.configYml.getInt("gui.close.location.column"),
                Slot.builder(
                    ItemStackBuilder(closeItem)
                        .setDisplayName(plugin.configYml.getString("gui.close.name"))
                        .build()
                ).onLeftClick { event, _ -> event.whoClicked.closeInventory() }
                    .build()
            )
            .build()
    }
}