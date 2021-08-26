package com.willfp.ecoskills.skills

import com.willfp.ecoskills.api.PlayerSkillLevelUpEvent
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import com.willfp.ecoskills.getStatLevel
import com.willfp.ecoskills.setEffectLevel
import com.willfp.ecoskills.setStatLevel
import com.willfp.ecoskills.stats.Stat
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class SkillLevellingListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onLevelUp(event: PlayerSkillLevelUpEvent) {
        val player = event.player
        val skill = event.skill
        val to = event.level

        for (reward in skill.getLevelUpRewards()) {
            val obj = reward.obj
            val toGive = skill.getLevelUpReward(obj, to)

            when (obj) {
                is Effect -> {
                    player.setEffectLevel(obj, player.getEffectLevel(obj) + toGive)
                }
                is Stat -> {
                    player.setStatLevel(obj, player.getStatLevel(obj) + toGive)
                }
            }
        }
    }
}