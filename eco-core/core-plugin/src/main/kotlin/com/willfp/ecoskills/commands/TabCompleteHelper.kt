package com.willfp.ecoskills.commands

import com.willfp.eco.core.config.updating.ConfigUpdater
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stat
import com.willfp.ecoskills.stats.Stats
import java.util.stream.Collectors

object TabCompleteHelper {
    /**
     * Skill names.
     */
    val SKILL_NAMES: MutableList<String> = ArrayList()

    /**
     * Stat names.
     */
    val STAT_NAMES: MutableList<String> = ArrayList()

    /**
     * Amounts.
     */
    val AMOUNTS = listOf(
        "1",
        "2",
        "3",
        "4",
        "5",
        "10"
    )

    /**
     * Numbers.
     */
    val NUMBERS = listOf(
        "1",
        "2",
        "3",
        "4",
        "5"
    )

    /**
     * Update lists.
     */
    @JvmStatic
    fun update() {
        SKILL_NAMES.clear()
        SKILL_NAMES.addAll(
            Skills.values().stream().filter(Skill::enabled).map(Skill::id).collect(Collectors.toList())
        )
        STAT_NAMES.clear()
        STAT_NAMES.addAll(
            Stats.values().stream().map(Stat::id).collect(Collectors.toList())
        )
    }
}