package com.willfp.ecoskills.skills

import com.willfp.eco.core.EcoPlugin
import com.willfp.ecoskills.*
import com.willfp.ecoskills.api.PlayerSkillExpGainEvent
import com.willfp.ecoskills.api.PlayerSkillLevelUpEvent
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.stats.Stat
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class SkillLevellingListener(
    private val plugin: EcoPlugin
) : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onLevelUp(event: PlayerSkillLevelUpEvent) {
        val player = event.player
        val skill = event.skill
        val to = event.level

        for (reward in skill.getLevelUpRewards()) {
            if (reward.obj is Effect) {
                player.setEffectLevel(reward.obj, player.getEffectLevel(reward.obj) + skill.getLevelUpReward(reward.obj, to))
            }
            if (reward.obj is Stat) {
                player.setStatLevel(reward.obj, player.getStatLevel(reward.obj) + skill.getLevelUpReward(reward.obj, to))
            }
        }
    }
}