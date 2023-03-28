package com.willfp.ecoskills.effects;

import com.google.common.collect.ImmutableSet;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.registry.Registry;
import com.willfp.libreforge.loader.LibreforgePlugin;
import com.willfp.libreforge.loader.configs.ConfigCategory;
import com.willfp.libreforge.loader.configs.LegacyLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public final class CustomEffects extends ConfigCategory {
    /**
     * Custom effects.
     */
    private static final Registry<CustomEffect> REGISTRY = new Registry<>();

    public CustomEffects() {
        super("effect", "customeffects");
    }

    @Override
    public LegacyLocation getLegacyLocation() {
        return new LegacyLocation(
                "customeffects.yml",
                "effects",
                new ArrayList<>()
        );
    }

    /**
     * Get all registered custom effects.
     *
     * @return The custom effects.
     */
    public static Collection<CustomEffect> values() {
        return ImmutableSet.copyOf(REGISTRY.values());
    }

    @Override
    public void clear(@NotNull final LibreforgePlugin libreforgePlugin) {
        for (Effect effect : REGISTRY.values()) {
            Effects.removeEffect(effect);
        }

        REGISTRY.clear();
    }

    @Override
    public void acceptConfig(@NotNull final LibreforgePlugin libreforgePlugin,
                             @NotNull final String id,
                             @NotNull final Config config) {
        REGISTRY.register(new CustomEffect(id, config));
    }
}
