package com.willfp.ecoskills.api;

import com.willfp.ecoskills.api.modifier.ItemStatModifier;
import com.willfp.ecoskills.api.modifier.PlayerStatModifier;
import com.willfp.ecoskills.effects.Effect;
import com.willfp.ecoskills.skills.Skill;
import com.willfp.ecoskills.stats.Stat;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface EcoSkillsAPI {
    /**
     * Get a player's level of a certain skill.
     *
     * @param player The player.
     * @param skill  The skill.
     * @return The level.
     */
    int getSkillLevel(@NotNull OfflinePlayer player,
                      @NotNull Skill skill);

    /**
     * Give skill experience to a player.
     *
     * @param player The player.
     * @param skill  The skill.
     * @param amount The amount of experience to give.
     */
    void giveSkillExperience(@NotNull Player player,
                             @NotNull Skill skill,
                             double amount);

    /**
     * Give skill experience to a player.
     *
     * @param player           The player.
     * @param skill            The skill.
     * @param amount           The amount of experience to give.
     * @param applyMultipliers If multipliers should be applied.
     */
    void giveSkillExperience(@NotNull Player player,
                             @NotNull Skill skill,
                             double amount,
                             boolean applyMultipliers);

    /**
     * Get progress to next level between 0 and 1, where 0 is none and 1 is complete.
     *
     * @param player The player.
     * @param skill  The skill.
     * @return The progress.
     */
    double getSkillProgressToNextLevel(@NotNull OfflinePlayer player,
                                       @NotNull Skill skill);

    /**
     * Get the experience required to advance to the next level.
     *
     * @param player The player.
     * @param skill  The skill.
     * @return The experience required.
     */
    int getSkillProgressRequired(@NotNull OfflinePlayer player,
                                 @NotNull Skill skill);

    /**
     * Get experience to the next level.
     *
     * @param player The player.
     * @param skill  The skill.
     * @return The experience.
     */
    double getSkillProgress(@NotNull OfflinePlayer player,
                            @NotNull Skill skill);

    /**
     * Get the average skill level.
     *
     * @param player The player.
     * @return The average skill level.
     */
    double getAverageSkillLevel(@NotNull OfflinePlayer player);

    /**
     * Get the total skill level.
     *
     * @param player The player.
     * @return The total skill level.
     */
    int getTotalSkillLevel(@NotNull OfflinePlayer player);

    /**
     * Get the effect level for player.
     *
     * @param player The player.
     * @param effect The effect.
     * @return The level.
     */
    int getEffectLevel(@NotNull OfflinePlayer player,
                       @NotNull Effect effect);

    /**
     * Get the stat level for a player.
     *
     * @param player The player.
     * @param stat   The stat.
     * @return The stat level.
     */
    int getStatLevel(@NotNull OfflinePlayer player,
                     @NotNull Stat stat);

    /**
     * Get the stat level for a player before any modifiers are applied.
     *
     * @param player The player.
     * @param stat   The stat.
     * @return The stat level.
     */
    int getBaseStatLevel(@NotNull OfflinePlayer player,
                         @NotNull Stat stat);

    /**
     * Get the added stat levels for a player from modifiers.
     *
     * @param player The player.
     * @param stat   The stat.
     * @return The stat level.
     */
    int getBonusStatLevel(@NotNull Player player,
                          @NotNull Stat stat);

    /**
     * Add a stat modifier to an item.
     *
     * @param itemStack The item.
     * @param modifier  The modifier.
     */
    void addStatModifier(@NotNull ItemStack itemStack,
                         @NotNull ItemStatModifier modifier);

    /**
     * Remove a stat modifier from an item.
     *
     * @param itemStack The item.
     * @param modifier  The modifier.
     */
    void removeStatModifier(@NotNull ItemStack itemStack,
                            @NotNull ItemStatModifier modifier);

    /**
     * Get stat modifier keys on an item.
     *
     * @param itemStack The item.
     * @return The modifier keys.
     */
    Set<NamespacedKey> getStatModifierKeys(@NotNull ItemStack itemStack);

    /**
     * Get stat modifiers on an item.
     *
     * @param itemStack The item.
     * @return The modifiers.
     */
    Set<ItemStatModifier> getStatModifiers(@NotNull ItemStack itemStack);

    /**
     * Get stat modifier on an item.
     *
     * @param itemStack The item.
     * @param key       The key
     * @return The modifier.
     */
    @Nullable
    ItemStatModifier getStatModifier(@NotNull ItemStack itemStack,
                                     @NotNull NamespacedKey key);

    /**
     * Add a stat modifier to a player.
     *
     * @param player   The player.
     * @param modifier The modifier.
     */
    void addStatModifier(@NotNull Player player,
                         @NotNull PlayerStatModifier modifier);

    /**
     * Remove a stat modifier from a player.
     *
     * @param player   The player.
     * @param modifier The modifier.
     */
    void removeStatModifier(@NotNull Player player,
                            @NotNull PlayerStatModifier modifier);

    /**
     * Get stat modifier keys on a player.
     *
     * @param player The player.
     * @return The modifier keys.
     */
    Set<NamespacedKey> getStatModifierKeys(@NotNull Player player);

    /**
     * Get stat modifiers on a player.
     *
     * @param player The player.
     * @return The modifiers.
     */
    Set<PlayerStatModifier> getStatModifiers(@NotNull Player player);

    /**
     * Get stat modifier on a player.
     *
     * @param player The player.
     * @param key    The key.
     * @return The modifier.
     */
    @Nullable
    PlayerStatModifier getStatModifier(@NotNull Player player,
                                       @NotNull NamespacedKey key);

    /**
     * Get the instance of the API.
     *
     * @return The API.
     */
    static EcoSkillsAPI getInstance() {
        return EcoSkillsAPIImpl.INSTANCE;
    }
}
