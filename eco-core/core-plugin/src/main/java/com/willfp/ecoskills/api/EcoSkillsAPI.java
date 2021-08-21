package com.willfp.ecoskills.api;

import com.willfp.ecoskills.effects.Effect;
import com.willfp.ecoskills.skills.Skill;
import com.willfp.ecoskills.stats.Stat;
import org.bukkit.NamespacedKey;
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
    int getSkillLevel(@NotNull Player player,
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
     * Get progress to next level between 0 and 1, where 0 is none and 1 is complete.
     *
     * @param player The player.
     * @param skill  The skill.
     * @return The progress.
     */
    double getSkillProgressToNextLevel(@NotNull Player player,
                                       @NotNull Skill skill);

    /**
     * Get the experience required to advance to the next level.
     *
     * @param player The player.
     * @param skill  The skill.
     * @return The experience required.
     */
    int getSkillProgressRequired(@NotNull Player player,
                                 @NotNull Skill skill);

    /**
     * Get experience to the next level.
     *
     * @param player The player.
     * @param skill  The skill.
     * @return The experience.
     */
    double getSkillProgress(@NotNull Player player,
                            @NotNull Skill skill);

    /**
     * Get the effect level for player.
     *
     * @param player The player.
     * @param effect The effect.
     * @return The level.
     */
    int getEffectLevel(@NotNull Player player,
                       @NotNull Effect effect);

    /**
     * Get the stat level for a player.
     *
     * @param player The player.
     * @param stat   The stat.
     * @return The stat level.
     */
    int getStatLevel(@NotNull Player player,
                     @NotNull Stat stat);

    /**
     * Add a stat modifier to an item.
     *
     * @param itemStack The item.
     * @param modifier  The modifier.
     */
    void addStatModifier(@NotNull ItemStack itemStack,
                         @NotNull StatModifier modifier);

    /**
     * Remove a stat modifier from an item.
     *
     * @param itemStack The item.
     * @param modifier  The modifier.
     */
    void removeStatModifier(@NotNull ItemStack itemStack,
                            @NotNull StatModifier modifier);

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
    Set<StatModifier> getStatModifiers(@NotNull ItemStack itemStack);

    /**
     * Get stat modifier on an item.
     *
     * @param itemStack The item.
     * @param key The key
     * @return The modifier.
     */
    @Nullable
    StatModifier getStatModifier(@NotNull ItemStack itemStack,
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
