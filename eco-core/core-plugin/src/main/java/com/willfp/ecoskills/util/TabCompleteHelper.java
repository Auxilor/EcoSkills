package com.willfp.ecoskills.util;

import com.willfp.eco.core.config.updating.ConfigUpdater;
import com.willfp.ecoskills.skills.Skill;
import com.willfp.ecoskills.skills.Skills;
import com.willfp.ecoskills.stats.Stat;
import com.willfp.ecoskills.stats.Stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TabCompleteHelper {
    /**
     * Skill names.
     */
    public static final List<String> SKILL_NAMES = new ArrayList<>();

    /**
     * Stat names.
     */
    public static final List<String> STAT_NAMES = new ArrayList<>();

    /**
     * Amounts.
     */
    public static final List<String> AMOUNTS = Arrays.asList(
            "1",
            "2",
            "3",
            "4",
            "5",
            "10"
    );

    /**
     * Numbers.
     */
    public static final List<String> NUMBERS = Arrays.asList(
            "1",
            "2",
            "3",
            "4",
            "5"
    );

    /**
     * Update lists.
     */
    @ConfigUpdater
    public static void update() {
        SKILL_NAMES.clear();
        SKILL_NAMES.addAll(
                Skills.values().stream().map(Skill::getId).collect(Collectors.toList())
        );
        STAT_NAMES.clear();
        STAT_NAMES.addAll(
                Stats.values().stream().map(Stat::getId).collect(Collectors.toList())
        );
    }
}
