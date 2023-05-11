package com.willfp.ecoskills.skills

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.afk.AFKManager
import com.willfp.ecoskills.api.gainSkillXP
import com.willfp.libreforge.counters.Accumulator
import org.bukkit.entity.Player

class SkillXPAccumulator(
    private val plugin: EcoPlugin,
    private val skill: Skill
) : Accumulator {
    override fun accept(player: Player, count: Double) {
        if (plugin.configYml.getBool("skills.prevent-levelling-while-afk") && AFKManager.isAfk(player)) {
            return
        }

        player.gainSkillXP(skill, count)
    }
}
