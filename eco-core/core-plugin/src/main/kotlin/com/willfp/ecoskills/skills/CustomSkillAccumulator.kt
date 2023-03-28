package com.willfp.ecoskills.skills

import com.willfp.ecoskills.giveSkillExperience
import com.willfp.libreforge.counters.Accumulator
import org.bukkit.entity.Player

class CustomSkillAccumulator(
    private val skill: CustomSkill
): Accumulator {
    override fun accept(player: Player, count: Double) {
        if (!skill.isEnabledFor(player)) {
            return
        }

        player.giveSkillExperience(skill, count)
    }
}
