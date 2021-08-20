package com.willfp.ecoskills.skills;

import com.google.common.collect.ImmutableSet;
import com.willfp.eco.core.config.updating.ConfigUpdater;
import com.willfp.ecoskills.skills.skills.SkillMining;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Skills {
    /**
     * All registered Skills.
     */
    private static final Map<String, Skill> REGISTRY = new HashMap<>();

    public static final Skill MINING = new SkillMining();
    /*
    public static final Skill COMBAT = new Skill(PLUGIN, "combat");
    public static final Skill ENCHANTING = new Skill(PLUGIN, "enchanting");
    public static final Skill FARMING = new Skill(PLUGIN, "farming");
    public static final Skill WOODCUTTING = new Skill(PLUGIN, "woodcutting");
    public static final Skill FISHING = new Skill(PLUGIN, "fishing");
    public static final Skill ALCHEMY = new Skill(PLUGIN, "alchemy");
    public static final Skill ARMORY = new Skill(PLUGIN, "armory");
    public static final Skill EXPLORATION = new Skill(PLUGIN, "exploration");
     */

    @ApiStatus.Internal
    public static void registerNewSkill(@NotNull final Skill skill) {
        REGISTRY.put(skill.getId(), skill);
    }

    @Nullable
    public static Skill getByID(@NotNull final String id) {
        return REGISTRY.get(id.toLowerCase());
    }

    @Nullable
    public static Skill getByKey(@NotNull final NamespacedKey key) {
        return REGISTRY.get(key.getKey());
    }

    public static Set<Skill> values() {
        return ImmutableSet.copyOf(REGISTRY.values());
    }

    @ConfigUpdater
    public static void update() {
        for (Skill skill : Skills.values()) {
            skill.update();
        }
    }
}
