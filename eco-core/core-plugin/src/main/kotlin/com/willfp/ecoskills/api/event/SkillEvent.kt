package com.willfp.ecoskills.api.event

import com.willfp.ecoskills.skills.Skill

interface SkillEvent {
    val skill: Skill
}
