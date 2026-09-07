package com.willfp.ecoskills.skills

import com.willfp.eco.core.progression.LevelProgression
import com.willfp.eco.core.progression.StopReason
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
        if (!xp.isFinite() || xp < 0.0) {
            return
        }

        val current = this[skill]
        val change = LevelProgression.progress(skill.curve, current.level, current.xp, xp)

        if (change.stopReason == StopReason.INVALID_REQUIREMENT) {
            skill.warnBrokenCurveOnce(current.level + 1)
        }

        this[skill] = SkillLevel(change.newLevel, change.newXp)

        val gained = change.levelsGained ?: return

        for (level in gained) {
            if (player is Player) {
                Bukkit.getPluginManager().callEvent(PlayerSkillLevelUpEvent(player, skill, level))
            }

            skill.handleLevelUp(player, level)
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
