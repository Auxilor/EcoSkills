package com.willfp.ecoskills.skills;

import com.google.common.collect.ImmutableSet;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.registry.Registry;
import com.willfp.libreforge.loader.LibreforgePlugin;
import com.willfp.libreforge.loader.configs.ConfigCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public final class CustomSkills extends ConfigCategory {
    /**
     * Custom skills.
     */
    private static final Registry<CustomSkill> REGISTRY = new Registry<>();

    public CustomSkills() {
        super("skill", "customskills");
    }

    /**
     * Get all registered custom skills.
     *
     * @return The custom skills.
     */
    public static Collection<CustomSkill> values() {
        return ImmutableSet.copyOf(REGISTRY.values());
    }

    @Override
    public void clear(@NotNull final LibreforgePlugin libreforgePlugin) {
        for (Skill skill : REGISTRY.values()) {
            Skills.removeSkill(skill);
        }

        REGISTRY.clear();
    }

    @Override
    public void acceptConfig(@NotNull final LibreforgePlugin libreforgePlugin,
                             @NotNull final String id,
                             @NotNull final Config config) {
        REGISTRY.register(new CustomSkill(id, config));
    }
}
