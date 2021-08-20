package com.willfp.ecoskills

import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.stats.Stat
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

fun Player.getSkillLevel(skill: Skill): Int {
    return this.persistentDataContainer.getOrDefault(skill.key, PersistentDataType.INTEGER, 0)
}

fun Player.setSkillLevel(skill: Skill, level: Int) {
    this.persistentDataContainer.set(skill.key, PersistentDataType.INTEGER, level)
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