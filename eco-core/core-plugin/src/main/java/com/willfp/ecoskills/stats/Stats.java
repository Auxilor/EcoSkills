package com.willfp.ecoskills.stats;

import com.google.common.collect.ImmutableSet;
import com.willfp.eco.core.config.updating.ConfigUpdater;
import com.willfp.ecoskills.stats.stats.StatCritChance;
import com.willfp.ecoskills.stats.stats.StatCritDamage;
import com.willfp.ecoskills.stats.stats.StatDefense;
import com.willfp.ecoskills.stats.stats.StatSpeed;
import com.willfp.ecoskills.stats.stats.StatStrength;
import com.willfp.ecoskills.stats.stats.StatWisdom;
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

    public static final Stat DEFENSE = new StatDefense();
    public static final Stat STRENGTH = new StatStrength();
    public static final Stat CRIT_CHANCE = new StatCritChance();
    public static final Stat CRIT_DAMAGE = new StatCritDamage();
    public static final Stat SPEED = new StatSpeed();
    public static final Stat WISDOM = new StatWisdom();

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
