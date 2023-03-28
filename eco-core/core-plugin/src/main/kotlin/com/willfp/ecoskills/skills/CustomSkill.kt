package com.willfp.ecoskills.skills

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.counters.Counters
import org.bukkit.entity.Player

class CustomSkill(
    id: String,
    config: Config
) : Skill(
    id,
    forceConfig = config
) {
    private val jobXpGains = config.getSubsections("xp-gain-methods").mapNotNull {
        Counters.compile(it, ViolationContext(plugin, "Custom Skill $id"))
    }

    override fun onRegister() {
        jobXpGains.forEach { it.bind(CustomSkillAccumulator(this)) }
    }

    override fun onRemove() {
        jobXpGains.forEach { it.unbind() }
    }

    fun isEnabledFor(player: Player): Boolean = player.filterSkillEnabled() != null
}
