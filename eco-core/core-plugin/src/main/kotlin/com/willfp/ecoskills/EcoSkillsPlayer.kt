package com.willfp.ecoskills

import com.willfp.ecoskills.api.PlayerSkillExpGainEvent
import com.willfp.ecoskills.api.PlayerSkillLevelUpEvent
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.stats.Stat
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

fun Player.giveSkillExperience(skill: Skill, experience: Double) {
    val gainEvent = PlayerSkillExpGainEvent(this, skill, experience)
    Bukkit.getPluginManager().callEvent(gainEvent)

    if (gainEvent.isCancelled) {
        return
    }

    val level = this.getSkillLevel(skill)

    this.setSkillProgress(skill, this.getSkillProgress(skill) + experience)

    if (this.getSkillProgress(skill) >= skill.getExpForLevel(level + 1)) {
        this.setSkillProgress(skill, 0.0)
        this.setSkillLevel(skill, level + 1)
        val levelUpEvent = PlayerSkillLevelUpEvent(this, skill, level + 1)
        Bukkit.getPluginManager().callEvent(levelUpEvent)
    }
}

fun Player.getSkillLevel(skill: Skill): Int {
    return this.persistentDataContainer.getOrDefault(skill.key, PersistentDataType.INTEGER, 0)
}

fun Player.setSkillLevel(skill: Skill, level: Int) {
    this.persistentDataContainer.set(skill.key, PersistentDataType.INTEGER, level)
}

fun Player.getSkillProgressToNextLevel(skill: Skill): Double {
    return this.getSkillProgress(skill) / this.getSkillProgressRequired(skill)
}

fun Player.getSkillProgressRequired(skill: Skill): Int {
    return skill.getExpForLevel(this.getSkillLevel(skill) + 1)
}

fun Player.getSkillProgress(skill: Skill): Double {
    return this.persistentDataContainer.getOrDefault(skill.xpKey, PersistentDataType.DOUBLE, 0.0)
}

fun Player.setSkillProgress(skill: Skill, level: Double) {
    this.persistentDataContainer.set(skill.xpKey, PersistentDataType.DOUBLE, level)
}

fun Player.getEffectLevel(effect: Effect): Int {
    return this.persistentDataContainer.getOrDefault(effect.key, PersistentDataType.INTEGER, 0)
}

fun Player.setEffectLevel(effect: Effect, level: Int) {
    this.persistentDataContainer.set(effect.key, PersistentDataType.INTEGER, level)
}

fun Player.getStatLevel(stat: Stat): Int {
    return this.persistentDataContainer.getOrDefault(stat.key, PersistentDataType.INTEGER, 0)
}

fun Player.setStatLevel(stat: Stat, level: Int) {
    this.persistentDataContainer.set(stat.key, PersistentDataType.INTEGER, level)
    stat.updateStatLevel(this)
}