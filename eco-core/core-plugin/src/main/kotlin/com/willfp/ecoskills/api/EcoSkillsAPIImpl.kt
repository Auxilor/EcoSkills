package com.willfp.ecoskills.api

import com.willfp.ecoskills.*
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.stats.Stat
import org.bukkit.entity.Player

object EcoSkillsAPIImpl: EcoSkillsAPI {
    override fun getSkillLevel(player: Player, skill: Skill): Int {
        return player.getSkillLevel(skill)
    }

    override fun giveSkillExperience(player: Player, skill: Skill, amount: Double) {
        player.giveSkillExperience(skill, amount)
    }

    override fun getSkillProgressToNextLevel(player: Player, skill: Skill): Double {
        return player.getSkillProgressToNextLevel(skill)
    }

    override fun getSkillProgressRequired(player: Player, skill: Skill): Int {
        return player.getSkillProgressRequired(skill)
    }

    override fun getSkillProgress(player: Player, skill: Skill): Double {
        return player.getSkillProgress(skill)
    }

    override fun getEffectLevel(player: Player, effect: Effect): Int {
        return player.getEffectLevel(effect)
    }

    override fun getStatLevel(player: Player, stat: Stat): Int {
        return player.getStatLevel(stat)
    }
}