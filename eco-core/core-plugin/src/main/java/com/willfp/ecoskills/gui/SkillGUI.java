package com.willfp.ecoskills.gui;

import com.willfp.eco.core.config.updating.ConfigUpdater;
import com.willfp.eco.core.gui.menu.Menu;
import com.willfp.eco.core.gui.slot.FillerMask;
import com.willfp.eco.core.gui.slot.MaskMaterials;
import com.willfp.eco.core.gui.slot.Slot;
import com.willfp.eco.core.items.builder.ItemStackBuilder;
import com.willfp.eco.core.items.builder.SkullBuilder;
import com.willfp.eco.util.StringUtils;
import com.willfp.ecoskills.EcoSkillsPlugin;
import com.willfp.ecoskills.skills.Skill;
import com.willfp.ecoskills.skills.Skills;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class SkillGUI {
    /**
     * The home menu.
     */
    private static Menu homeMenu;

    /**
     * Get the home menu.
     *
     * @return The home menu.
     */
    public static Menu getHomeMenu() {
        return homeMenu;
    }

    /**
     * Update the menus.
     *
     * @param plugin The plugin.
     */
    @ConfigUpdater
    public static void update(@NotNull final EcoSkillsPlugin plugin) {
        homeMenu = buildHomeMenu(plugin);
    }

    private static Menu buildHomeMenu(@NotNull final EcoSkillsPlugin plugin) {
        String[] maskPattern = plugin.getConfigYml().getStrings("gui.mask.pattern", false).toArray(new String[0]);
        Material[] maskMaterials = plugin.getConfigYml()
                .getStrings("gui.mask.materials", false)
                .stream()
                .map(string -> Material.getMaterial(string.toUpperCase()))
                .filter(Objects::nonNull)
                .toArray(Material[]::new);


        Material closeMaterial = Material.getMaterial(plugin.getConfigYml().getString("gui.close.material", false).toUpperCase());
        assert closeMaterial != null;

        Function<Player, ItemStack> playerHeadItemBuilder = player -> {
            ItemStack itemStack = new SkullBuilder()
                    .setDisplayName(plugin.getConfigYml().getString("gui.player-info.name").replace("%player%", player.getDisplayName()))
                    .addLoreLines(() -> {
                        List<String> lore = new ArrayList<>();

                        for (String string : plugin.getConfigYml().getStrings("gui.player-info.lore", false)) {
                            lore.add(StringUtils.format(string, player));
                        }

                        return lore;
                    })
                    .build();
            SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
            assert meta != null;
            meta.setOwningPlayer(player);
            itemStack.setItemMeta(meta);

            return itemStack;
        };

        return Menu.builder(plugin.getConfigYml().getInt("gui.rows"))
                .setTitle(plugin.getLangYml().getString("menu.title"))
                .setMask(
                        new FillerMask(
                                new MaskMaterials(
                                        maskMaterials
                                ),
                                maskPattern
                        )
                )
                .setSlot(
                        plugin.getConfigYml().getInt("gui.player-info.row"),
                        plugin.getConfigYml().getInt("gui.player-info.column"),
                        Slot.builder(playerHeadItemBuilder).build()
                )
                .modfiy(menuBuilder -> {
                    for (Skill skill : Skills.values()) {
                        menuBuilder.setSlot(
                                skill.getConfig().getInt("gui.position.row"),
                                skill.getConfig().getInt("gui.position.column"),
                                skill.getGui().getSlot()
                        );
                    }
                })
                .setSlot(plugin.getConfigYml().getInt("gui.close.location.row"),
                        plugin.getConfigYml().getInt("gui.close.location.column"),
                        Slot.builder(
                                new ItemStackBuilder(closeMaterial)
                                        .setDisplayName(plugin.getConfigYml().getString("gui.close.name"))
                                        .build()
                        ).onLeftClick((event, slot) -> {
                            event.getWhoClicked().closeInventory();
                        }).build()
                )
                .build();
    }
}
