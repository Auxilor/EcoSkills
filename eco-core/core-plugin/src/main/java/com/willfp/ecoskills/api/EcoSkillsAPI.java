package com.willfp.ecoskills.api;

import com.willfp.ecoskills.effects.Effect;
import com.willfp.ecoskills.skills.Skill;
import com.willfp.ecoskills.stats.Stat;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
     * Get the instance of the API.
     *
     * @return The API.
     */
    static EcoSkillsAPI getInstance() {
        return EcoSkillsAPIImpl.INSTANCE;
    }
}
