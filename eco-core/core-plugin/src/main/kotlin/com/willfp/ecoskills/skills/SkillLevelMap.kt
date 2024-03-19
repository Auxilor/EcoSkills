package com.willfp.ecoskills.skills

import com.willfp.ecoskills.api.event.PlayerSkillLevelUpEvent
import com.willfp.ecoskills.api.event.PlayerSkillXPGainEvent
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class SkillLevelMap(
    private val player: OfflinePlayer
) {
    operator fun get(skill: Skill): SkillLevel {
        return SkillLevel(
            skill.getSavedLevel(player),
            skill.getSavedXP(player)
        )
    }

    operator fun set(skill: Skill, level: SkillLevel) {
        require(level.level >= 0) { "Level must be positive" }
        require(level.xp >= 0) { "XP must be positive" }

        skill.setSavedLevel(player, level.level)
        skill.setSavedXP(player, level.xp)
    }

    fun giveXP(skill: Skill, xp: Double) {
        require(xp >= 0) { "XP must be positive" }

        val current = this[skill]

        val required = skill.getXPRequired(current.level)

        return if (current.xp + xp >= required && current.level < skill.maxLevel) {
            val overshoot = current.xp + xp - required

            this[skill] = SkillLevel(
                current.level + 1,
                0.0
            )

            if (player is Player) {
                Bukkit.getPluginManager().callEvent(
                    PlayerSkillLevelUpEvent(
                        player,
                        skill,
                        current.level + 1
                    )
                )
            }

            skill.handleLevelUp(player, current.level + 1)

            giveXP(skill, overshoot) // For recursive level gains.
        } else {
            this[skill] = SkillLevel(
                current.level,
                current.xp + xp
            )
        }
    }

    fun gainXP(skill: Skill, xp: Double) {
        require(xp >= 0) { "XP must be positive" }

        if (player is Player) {
            val event = PlayerSkillXPGainEvent(
                player,
                skill,
                xp * player.skillXPMultiplier
            )

            Bukkit.getPluginManager().callEvent(event)

            if (!event.isCancelled) {
                giveXP(skill, event.gainedXP)
            }
        } else {
            giveXP(skill, xp)
        }
    }

    fun reset(skill: Skill) {
        this[skill] = SkillLevel(
            skill.startLevel,
            0.0
        )
    }
}
