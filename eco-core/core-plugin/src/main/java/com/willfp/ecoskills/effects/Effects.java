package com.willfp.ecoskills.effects;

import com.google.common.collect.ImmutableSet;
import com.willfp.eco.core.config.updating.ConfigUpdater;
import com.willfp.ecoskills.EcoSkillsPlugin;
import com.willfp.ecoskills.skills.Skill;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Effects {
    /**
     * All registered Skills.
     */
    private static final Map<String, Effect> REGISTRY = new HashMap<>();

    /**
     * Instance of EcoSkills.
     */
    private static final EcoSkillsPlugin PLUGIN = EcoSkillsPlugin.getInstance();

    public static final Skill MINING = new Skill(PLUGIN, "mining");
    public static final Skill COMBAT = new Skill(PLUGIN, "combat");
    public static final Skill ENCHANTING = new Skill(PLUGIN, "enchanting");
    public static final Skill FARMING = new Skill(PLUGIN, "farming");
    public static final Skill WOODCUTTING = new Skill(PLUGIN, "woodcutting");
    public static final Skill FISHING = new Skill(PLUGIN, "fishing");
    public static final Skill ALCHEMY = new Skill(PLUGIN, "alchemy");
    public static final Skill ARMORY = new Skill(PLUGIN, "armory");
    public static final Skill EXPLORATION = new Skill(PLUGIN, "exploration");

    @ApiStatus.Internal
    public static void registerNewEffect(@NotNull final Effect effect) {
        REGISTRY.put(effect.getId(), effect);
    }

    @Nullable
    public static Effect getByID(@NotNull final String id) {
        return REGISTRY.get(id.toLowerCase());
    }

    @Nullable
    public static Effect getByKey(@NotNull final NamespacedKey key) {
        return REGISTRY.get(key.getKey());
    }

    public static Set<Effect> values() {
        return ImmutableSet.copyOf(REGISTRY.values());
    }

    @ConfigUpdater
    public static void update() {
        for (Effect effect : Effects.values()) {
            effect.update();
        }
    }
}
