package com.willfp.ecoskills

import com.willfp.ecoskills.api.PlayerSkillExpGainEvent
import com.willfp.ecoskills.api.PlayerSkillLevelUpEvent
import com.willfp.ecoskills.data.storage.PlayerProfile.Companion.profile
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stat
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import java.util.*
import kotlin.math.abs

val expMultiplierCache = mutableMapOf<UUID, Double>()
val plugin: EcoSkillsPlugin = EcoSkillsPlugin.getInstance()

fun Player.getSkillExperienceMultiplier(): Double {
    if (expMultiplierCache.containsKey(this.uniqueId)) {
        return expMultiplierCache[this.uniqueId]!!
    }
    expMultiplierCache[this.uniqueId] = cacheSkillExperienceMultiplier()
    return this.getSkillExperienceMultiplier()
}

private fun Player.cacheSkillExperienceMultiplier(): Double {
    if (this.hasPermission("ecoskills.xpmultiplier.quadruple")) {
        return 4.0
    }

    if (this.hasPermission("ecoskills.xpmultiplier.triple")) {
        return 3.0
    }

    if (this.hasPermission("ecoskills.xpmultiplier.double")) {
        return 2.0
    }

    if (this.hasPermission("ecoskills.xpmultiplier.50percent")) {
        return 1.5
    }

    val prefix = "ecoskills.xpmultiplier."
    for (permissionAttachmentInfo in this.effectivePermissions) {
        val permission = permissionAttachmentInfo.permission
        if (permission.startsWith(prefix)) {
            return ((permission.substring(permission.lastIndexOf(".") + 1).toDoubleOrNull() ?: 100.0) / 100) + 1
        }
    }

    return 1.0
}

fun OfflinePlayer.getTotalSkillLevel(): Int {
    var total = 0
    for (skill in Skills.values()) {
        total += this.getSkillLevel(skill)
    }
    return total
}

fun OfflinePlayer.getAverageSkillLevel(): Double {
    var total = 0
    for (skill in Skills.values()) {
        total += this.getSkillLevel(skill)
    }
    return total / Skills.values().size.toDouble()
}

fun Player.giveSkillExperience(skill: Skill, experience: Double, noMultiply: Boolean = false) {
    val exp = abs(if (noMultiply) experience else experience * this.getSkillExperienceMultiplier())

    val gainEvent = PlayerSkillExpGainEvent(this, skill, exp)
    Bukkit.getPluginManager().callEvent(gainEvent)

    if (gainEvent.isCancelled) {
        return
    }

    val level = this.getSkillLevel(skill)

    this.setSkillProgress(skill, this.getSkillProgress(skill) + exp)

    if (this.getSkillProgress(skill) >= skill.getExpForLevel(level + 1) && level + 1 <= skill.maxLevel) {
        val overshoot = this.getSkillProgress(skill) - skill.getExpForLevel(level + 1)
        this.setSkillProgress(skill, 0.0)
        this.setSkillLevel(skill, level + 1)
        val levelUpEvent = PlayerSkillLevelUpEvent(this, skill, level + 1)
        Bukkit.getPluginManager().callEvent(levelUpEvent)
        this.giveSkillExperience(skill, overshoot, true)
    }
}

fun OfflinePlayer.getSkillLevel(skill: Skill): Int {
    return profile.readInt(skill.id)
}

fun OfflinePlayer.setSkillLevel(skill: Skill, level: Int) {
    return profile.write(skill.id, level)
}

fun OfflinePlayer.getSkillProgressToNextLevel(skill: Skill): Double {
    return this.getSkillProgress(skill) / this.getSkillProgressRequired(skill)
}

fun OfflinePlayer.getSkillProgressRequired(skill: Skill): Int {
    return skill.getExpForLevel(this.getSkillLevel(skill) + 1)
}

fun OfflinePlayer.getSkillProgress(skill: Skill): Double {
    return profile.readDouble(skill.xpKey.key)
}

fun OfflinePlayer.setSkillProgress(skill: Skill, level: Double) {
    profile.write(skill.xpKey.key, level)
}

fun OfflinePlayer.getEffectLevel(effect: Effect): Int {
    return profile.readInt(effect.id)
}

fun OfflinePlayer.setEffectLevel(effect: Effect, level: Int) {
    profile.write(effect.id, level)
}

fun OfflinePlayer.getStatLevel(stat: Stat): Int {
    var base = this.getBaseStatLevel(stat)
    if (this is Player) {
        base += this.getBonusStatLevel(stat)
    }
    return base
}

fun Player.getBonusStatLevel(stat: Stat): Int {
    var i = 0
    for (modifier in this.getStatModifiers()) {
        if (modifier.stat == stat) {
            i += modifier.amount
        }
    }
    return i
}

fun OfflinePlayer.getBaseStatLevel(stat: Stat): Int {
    return profile.readInt(stat.id)
}

fun Player.setStatLevel(stat: Stat, level: Int) {
    profile.write(stat.id, level)
    stat.updateStatLevel(this)
}

fun Entity.tryAsPlayer(): Player? {
    return when (this) {
        is Projectile -> {
            val shooter = this.shooter
            if (shooter is Player) shooter else null
        }
        is Player -> this
        else -> null
    }
}