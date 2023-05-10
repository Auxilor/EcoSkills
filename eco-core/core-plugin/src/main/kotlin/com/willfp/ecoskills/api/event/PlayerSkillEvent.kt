package com.willfp.ecoskills.api.event

import com.willfp.ecoskills.skills.Skill
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEvent

abstract class PlayerSkillEvent(
    who: Player,
    override val skill: Skill
) : PlayerEvent(who), SkillEvent
