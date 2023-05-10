package com.willfp.ecoskills.skills

import com.willfp.ecoskills.api.gainSkillXP
import com.willfp.libreforge.counters.Accumulator
import org.bukkit.entity.Player

class SkillXPAccumulator(
    private val skill: Skill
) : Accumulator {
    override fun accept(player: Player, count: Double) {
        player.gainSkillXP(skill, count)
    }
}
