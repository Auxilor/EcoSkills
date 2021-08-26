package com.willfp.ecoskills.data

import com.willfp.ecoskills.convertPersistentToYml
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getSkillLevel
import com.willfp.ecoskills.setEffectLevel
import com.willfp.ecoskills.skills.Skills
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class DataListener: Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.player.convertPersistentToYml()

        for (skill in Skills.values()) {
            for (levelUpReward in skill.getLevelUpRewards()) {
                val obj = levelUpReward.obj
                if (obj !is Effect) {
                    continue
                }
                event.player.setEffectLevel(obj, skill.getCumulativeLevelUpReward(obj, event.player.getSkillLevel(skill)))
            }
        }
    }
}