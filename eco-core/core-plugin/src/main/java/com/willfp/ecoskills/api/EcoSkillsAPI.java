package com.willfp.ecoskills.api;

import com.willfp.ecoskills.skills.Skill;
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
     * Get the instance of the API.
     *
     * @return The API.
     */
    static EcoSkillsAPI getInstance() {
        return EcoSkillsAPIImpl.INSTANCE;
    }
}
