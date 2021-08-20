package com.willfp.ecoskills.api;

import com.willfp.ecoskills.skills.Skill;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerSkillExpGainEvent extends PlayerEvent {
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
    private double amount;

    /**
     * Create a new PlayerSkillExpGainEvent.
     *
     * @param who    The player.
     * @param skill  The skill.
     * @param amount The amount of skill exp gained.
     */
    public PlayerSkillExpGainEvent(@NotNull final Player who,
                                   @NotNull final Skill skill,
                                   final double amount) {
        super(who);
        this.skill = skill;
        this.amount = amount;
    }

    /**
     * Get the skill for the event.
     *
     * @return The skill.
     */
    public Skill getSkill() {
        return this.skill;
    }

    /**
     * Get the amount of experience.
     *
     * @return The experience.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Set the amount of experience.
     *
     * @param amount The amount.
     */
    public void setAmount(final double amount) {
        this.amount = amount;
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
