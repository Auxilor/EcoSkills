package com.willfp.ecoskills.skills

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.events.TriggerPreProcessEvent
import com.willfp.libreforge.triggers.Counters
import org.bukkit.entity.Player

class CustomSkill(
    id: String,
    config: Config
) : Skill(
    id,
    forceConfig = config
) {
    private val jobXpGains = config.getSubsections("xp-gain-methods").mapNotNull {
        Counters.compile(it, "Skill $id")
    }

    fun getXP(event: TriggerPreProcessEvent): Double {
        return jobXpGains.sumOf { it.getCount(event) }
    }

    fun isEnabledFor(player: Player): Boolean = player.filterSkillEnabled() != null
}
