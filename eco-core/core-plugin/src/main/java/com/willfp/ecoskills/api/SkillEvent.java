package com.willfp.ecoskills.api;

import com.willfp.ecoskills.skills.Skill;
import org.jetbrains.annotations.NotNull;

public interface SkillEvent {
    /**
     * Get the skill.
     *
     * @return The skill.
     */
    @NotNull
    public Skill getSkill();
}
