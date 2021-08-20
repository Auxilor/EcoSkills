package com.willfp.ecoskills.api

import com.willfp.ecoskills.getSkillLevel
import com.willfp.ecoskills.skills.Skill
import org.bukkit.entity.Player

class EcoSkillsAPI {
    fun getSkillLevel(player: Player, skill: Skill): Int {
        return player.getSkillLevel(skill)
    }
}