package com.willfp.ecoskills.api;

import com.willfp.ecoskills.skills.Skill;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerSkillLevelUpEvent extends PlayerEvent implements SkillEvent {
    /**
     * Bukkit parity.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * The skill.
     */
    private final Skill skill;

    /**
     * The amount.
     */
    private final int level;

    /**
     * Create a new PlayerSkillLevelUpEvent.
     *
     * @param who   The player.
     * @param skill The skill.
     * @param level The level gained.
     */
    public PlayerSkillLevelUpEvent(@NotNull final Player who,
                                   @NotNull final Skill skill,
                                   final int level) {
        super(who);
        this.skill = skill;
        this.level = level;
    }

    /**
     * Get the skill for the event.
     *
     * @return The skill.
     */
    @Override
    @NotNull
    public Skill getSkill() {
        return this.skill;
    }

    /**
     * Get the level achieved.
     *
     * @return The level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Bukkit parity.
     *
     * @return The handler list.
     */
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Bukkit parity.
     *
     * @return The handler list.
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
