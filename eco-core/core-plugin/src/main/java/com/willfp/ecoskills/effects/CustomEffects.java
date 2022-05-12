package com.willfp.ecoskills.effects;

import com.google.common.collect.ImmutableSet;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.ecoskills.EcoSkillsPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class CustomEffects {
    /**
     * Custom effects.
     */
    private static final Set<CustomEffect> VALUES = new HashSet<>();

    /**
     * Get all registered custom effects.
     *
     * @return The custom effects.
     */
    public static Collection<CustomEffect> values() {
        return ImmutableSet.copyOf(VALUES);
    }

    /**
     * Update the registry.
     *
     * @param plugin Instance of EcoSkills.
     */
    public static void update(@NotNull final EcoSkillsPlugin plugin) {
        for (Effect effect : VALUES) {
            Effects.removeEffect(effect);
        }

        VALUES.clear();

        for (Config cfg : plugin.getCustomEffectsYml().getSubsections("effects")) {
            VALUES.add(new CustomEffect(cfg));
        }
    }
}
