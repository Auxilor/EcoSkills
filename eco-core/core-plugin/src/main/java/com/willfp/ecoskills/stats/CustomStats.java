package com.willfp.ecoskills.stats;

import com.google.common.collect.ImmutableSet;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.registry.Registry;
import com.willfp.libreforge.loader.LibreforgePlugin;
import com.willfp.libreforge.loader.configs.ConfigCategory;
import com.willfp.libreforge.loader.configs.LegacyLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public final class CustomStats extends ConfigCategory {
    /**
     * The registry.
     */
    private static final Registry<CustomStat> registry = new Registry<>();

    public CustomStats() {
        super("stat", "customstats");
    }

    @NotNull
    @Override
    public LegacyLocation getLegacyLocation() {
        return new LegacyLocation(
                "customstats.yml",
                "stats",
                new ArrayList<>()
        );
    }

    /**
     * Get all registered custom stats.
     *
     * @return The custom stats.
     */
    public static Collection<CustomStat> values() {
        return ImmutableSet.copyOf(registry.values());
    }

    @Override
    public void clear(@NotNull final LibreforgePlugin libreforgePlugin) {
        registry.values().forEach(Stats::removeStat);
        registry.clear();
    }

    @Override
    public void acceptConfig(@NotNull final LibreforgePlugin libreforgePlugin,
                             @NotNull final String id,
                             @NotNull final Config config) {
        registry.register(new CustomStat(id, config));
    }
}
