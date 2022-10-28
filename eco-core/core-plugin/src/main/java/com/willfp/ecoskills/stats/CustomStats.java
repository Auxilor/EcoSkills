package com.willfp.ecoskills.stats;

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

public final class CustomStats {
    /**
     * Custom stats.
     */
    private static final Set<CustomStat> VALUES = new HashSet<>();

    /**
     * Get all registered custom stats.
     *
     * @return The custom stats.
     */
    public static Collection<CustomStat> values() {
        return ImmutableSet.copyOf(VALUES);
    }

    /**
     * Update the registry.
     *
     * @param plugin Instance of EcoSkills.
     */
    public static void update(@NotNull final EcoSkillsPlugin plugin) {
        for (Stat stat : VALUES) {
            Stats.removeStat(stat);
        }

        VALUES.clear();

        for (Map.Entry<String, Config> entry : plugin.fetchConfigs("customstats", true).entrySet()) {
            VALUES.add(new CustomStat(entry.getKey(), entry.getValue()));
        }

        // Legacy
        Config customEffectsYml = Configs.fromFile(new File(plugin.getDataFolder(), "customstats.yml"), ConfigType.YAML);

        for (Config cfg : customEffectsYml.getSubsections("stats")) {
            VALUES.add(new CustomStat(cfg.getString("id"), cfg));
        }
    }
}
