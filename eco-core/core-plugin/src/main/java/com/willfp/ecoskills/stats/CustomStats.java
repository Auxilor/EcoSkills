package com.willfp.ecoskills.stats;

import com.google.common.collect.ImmutableSet;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.ecoskills.EcoSkillsPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
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

        for (Config cfg : plugin.getCustomStatsYml().getSubsections("stats")) {
            VALUES.add(new CustomStat(cfg));
        }
    }
}
