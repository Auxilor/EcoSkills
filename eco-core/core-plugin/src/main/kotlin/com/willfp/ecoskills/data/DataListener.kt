package com.willfp.ecoskills.data

import com.willfp.ecoskills.*
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stats
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.persistence.PersistentDataType

class DataListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.player.convertFromLegacyData()

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

        for (attribute in Attribute.values()) {
            val player = event.player

            val inst = player.getAttribute(attribute) ?: continue
            for (modifier in inst.modifiers.toMutableList()) {
                if (modifier.amount == 0.0) {
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

private fun Player.convertFromLegacyData() {
    for (effect in Effects.values()) {
        plugin.dataYml.set("player.${this.uniqueId}.${effect.id}", this.getEffectLevel(effect))
    }
    for (stat in Stats.values()) {
        plugin.dataYml.set("player.${this.uniqueId}.${stat.id}", this.getStatLevel(stat))
    }
    for (skill in Skills.values()) {
        plugin.dataYml.set("player.${this.uniqueId}.${skill.id}", this.getSkillLevel(skill))
        val prog = this.persistentDataContainer.get(skill.xpKey, PersistentDataType.DOUBLE)
        if (prog != null) {
            plugin.dataYml.set(
                "player.${this.uniqueId}.${skill.xpKey.key}",
                prog
            )
            this.persistentDataContainer.remove(skill.xpKey)
        }
    }
}