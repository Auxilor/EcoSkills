package com.willfp.ecoskills.skills;

import com.google.common.collect.ImmutableSet;
import com.willfp.eco.core.config.updating.ConfigUpdater;
import com.willfp.eco.core.placeholder.PlayerPlaceholder;
import com.willfp.eco.util.NumberUtils;
import com.willfp.ecoskills.EcoSkillsPlayerKt;
import com.willfp.ecoskills.EcoSkillsPlugin;
import com.willfp.ecoskills.skills.skills.SkillAlchemy;
import com.willfp.ecoskills.skills.skills.SkillArmory;
import com.willfp.ecoskills.skills.skills.SkillCombat;
import com.willfp.ecoskills.skills.skills.SkillEnchanting;
import com.willfp.ecoskills.skills.skills.SkillExploration;
import com.willfp.ecoskills.skills.skills.SkillFarming;
import com.willfp.ecoskills.skills.skills.SkillFishing;
import com.willfp.ecoskills.skills.skills.SkillMining;
import com.willfp.ecoskills.skills.skills.SkillWoodcutting;
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
    public static final Skill COMBAT = new SkillCombat();
    public static final Skill ENCHANTING = new SkillEnchanting();
    public static final Skill FARMING = new SkillFarming();
    public static final Skill WOODCUTTING = new SkillWoodcutting();
    public static final Skill EXPLORATION = new SkillExploration();
    public static final Skill ARMORY = new SkillArmory();
    public static final Skill FISHING = new SkillFishing();
    public static final Skill ALCHEMY = new SkillAlchemy();

    @ApiStatus.Internal
    public static void registerNewSkill(@NotNull final Skill skill) {
        REGISTRY.put(skill.getId(), skill);
    }

    @ApiStatus.Internal
    public static void removeSkill(@NotNull final Skill skill) {
        REGISTRY.remove(skill.getId());
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
    public static void update(@NotNull final EcoSkillsPlugin plugin) {
        new PlayerPlaceholder(
                plugin,
                "skill_multiplier",
                (player -> NumberUtils.format(EcoSkillsPlayerKt.getSkillExperienceMultiplier(player)))
        ).register();

        for (Skill skill : Skills.values()) {
            skill.update();
        }
    }

    static {
        update(EcoSkillsPlugin.getInstance());
    }
}
