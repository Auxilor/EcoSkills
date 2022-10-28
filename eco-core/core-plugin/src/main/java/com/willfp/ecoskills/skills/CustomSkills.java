package com.willfp.ecoskills.skills;

import com.google.common.collect.ImmutableSet;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.ecoskills.EcoSkillsPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class CustomSkills {
    /**
     * Custom skills.
     */
    private static final Set<CustomSkill> VALUES = new HashSet<>();

    /**
     * Get all registered custom skills.
     *
     * @return The custom skills.
     */
    public static Collection<CustomSkill> values() {
        return ImmutableSet.copyOf(VALUES);
    }

    /**
     * Update the registry.
     *
     * @param plugin Instance of EcoSkills.
     */
    public static void update(@NotNull final EcoSkillsPlugin plugin) {
        for (Skill skill : VALUES) {
            Skills.removeSkill(skill);
        }

        VALUES.clear();

        for (Map.Entry<String, Config> entry : plugin.fetchConfigs("customskills", false).entrySet()) {
            VALUES.add(new CustomSkill(entry.getKey(), entry.getValue()));
        }
    }
}
