package com.willfp.ecoskills.data

import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.getSkillLevel
import com.willfp.ecoskills.setEffectLevel
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stats
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent

class DataListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        
        for (ceffect in Effects.values()) {
            recount(player, ceffect)
        }

        for (attribute in Attribute.values()) {
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
            stat.updateStatLevel(player)
        }
    }
    
    private fun recount(player: Player, effect: Effect): Int {
        var total = 0
        for (skill in Skills.values()) {
            var ofSkill = 0
            val range = 1..player.getSkillLevel(skill)

            for (reward in skill.getLevelUpRewards()) {
                if (reward.obj is Effect && reward.obj == effect) {
                    for (i in range) {
                        val toGive = skill.getLevelUpReward(reward, i)
                        ofSkill+=toGive
                    }
                }
            }
            total+=ofSkill
        }
        player.setEffectLevel(effect, total)
        return total
    }
}