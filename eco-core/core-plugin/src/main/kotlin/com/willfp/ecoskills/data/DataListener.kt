package com.willfp.ecoskills.data

import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.convertPersistentToYml
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.getSkillLevel
import com.willfp.ecoskills.setEffectLevel
import com.willfp.ecoskills.skills.Skills
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.net.http.WebSocket

class DataListener(
    private val plugin: EcoSkillsPlugin
): Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.player.convertPersistentToYml()

        for (skill in Skills.values()) {
            for (effect in Effects.values()) {
                event.player.setEffectLevel(effect, skill.getCumulativeLevelUpReward(effect, event.player.getSkillLevel(skill)))
            }
        }
    }
}