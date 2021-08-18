package com.willfp.ecoskills.stats;

import com.google.common.collect.ImmutableSet;
import com.willfp.eco.core.config.updating.ConfigUpdater;
import com.willfp.ecoskills.EcoSkillsPlugin;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Stats {
    /**
     * All registered stats.
     */
    private static final Map<String, Stat> REGISTRY = new HashMap<>();

    /**
     * Instance of EcoSkills.
     */
    private static final EcoSkillsPlugin PLUGIN = EcoSkillsPlugin.getInstance();

    public static final Stat DEFENCE = new Stat(PLUGIN, "defence");
    public static final Stat STRENGTH = new Stat(PLUGIN, "strength");
    public static final Stat CRIT_CHANCE = new Stat(PLUGIN, "crit_chance");
    public static final Stat CRIT_DAMAGE = new Stat(PLUGIN, "crit_damage");
    public static final Stat SPEED = new Stat(PLUGIN, "speed");
    public static final Stat WISDOM = new Stat(PLUGIN, "wisdom");

    @ApiStatus.Internal
    public static void registerNewStat(@NotNull final Stat skill) {
        REGISTRY.put(skill.getId(), skill);
    }

    @Nullable
    public static Stat getByID(@NotNull final String id) {
        return REGISTRY.get(id.toLowerCase());
    }

    @Nullable
    public static Stat getByKey(@NotNull final NamespacedKey key) {
        return REGISTRY.get(key.getKey());
    }

    public static Set<Stat> values() {
        return ImmutableSet.copyOf(REGISTRY.values());
    }

    @ConfigUpdater
    public static void update() {
        for (Stat skill : Stats.values()) {
            skill.update();
        }
    }
}
