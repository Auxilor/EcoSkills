package com.willfp.ecoskills.data

import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.getSkillLevel
import com.willfp.ecoskills.setEffectLevel
import com.willfp.ecoskills.setStatLevel
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stat
import com.willfp.ecoskills.stats.Stats
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class DataListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        for (skill in Skills.values()) {
            for (levelUpReward in skill.getLevelUpRewards()) {
                val obj = levelUpReward.obj
                if (obj !is Effect) {
                    continue
                }
                event.player.setEffectLevel(
                    obj,
                    skill.getCumulativeLevelUpReward(obj, event.player.getSkillLevel(skill))
                )
            }
        }
        
        for (stat in Stats.values()) {
            var total = 0
            for (skill in Skills.values()) {
                for (levelUpReward in skill.getLevelUpRewards()) {
                    val obj = levelUpReward.obj
                    if (obj is Stat && obj == stat ) {
                        total+=skill.getCumulativeLevelUpReward(obj, event.player.getSkillLevel(skill))
                    }
                }
            }
            event.player.setStatLevel(stat, total)
        }

        for (attribute in Attribute.values()) {
            val player = event.player

            val inst = player.getAttribute(attribute) ?: continue
            for (modifier in inst.modifiers.toMutableList()) {
                if (modifier.amount == 0.0) {
                    inst.removeModifier(modifier)
                }

                if (modifier.uniqueId == Effects.ACCELERATED_ESCAPE.uuid) {
                    inst.removeModifier(modifier)
                }

                if (attribute == Attribute.GENERIC_MOVEMENT_SPEED) {
                    val name = modifier.name.toDoubleOrNull() ?: continue
                    if (name < 1.0 && name > 0.0) {
                        inst.removeModifier(modifier)
                    }
                }
            }
        }

        for (stat in Stats.values()) {
            stat.updateStatLevel(event.player)
        }
    }
}