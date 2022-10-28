package com.willfp.ecoskills.effects;

import com.google.common.collect.ImmutableSet;
import com.willfp.eco.core.config.ConfigType;
import com.willfp.eco.core.config.Configs;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.ecoskills.EcoSkillsPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
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

        for (Map.Entry<String, Config> entry : plugin.fetchConfigs("customeffects", true).entrySet()) {
            VALUES.add(new CustomEffect(entry.getKey(), entry.getValue()));
        }

        // Legacy
        Config customEffectsYml = Configs.fromFile(new File(plugin.getDataFolder(), "customeffects.yml"), ConfigType.YAML);

        for (Config cfg : customEffectsYml.getSubsections("effects")) {
            VALUES.add(new CustomEffect(cfg.getString("id"), cfg));
        }
    }
}
