package com.willfp.ecoskills

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.ecoskills.api.PlayerSkillExpGainEvent
import com.willfp.ecoskills.api.PlayerSkillLevelUpEvent
import com.willfp.ecoskills.api.modifier.ModifierOperation
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stat
import com.willfp.ecoskills.stats.Stats
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.entity.Tameable
import java.util.concurrent.TimeUnit
import kotlin.math.abs

private val expMultiplierCache = Caffeine.newBuilder()
    .expireAfterWrite(10, TimeUnit.SECONDS)
    .build<Player, Double> {
        it.cacheSkillExperienceMultiplier()
    }

private val plugin: EcoSkillsPlugin = EcoSkillsPlugin.getInstance()

fun Player.getSkillExperienceMultiplier(): Double {
    return expMultiplierCache.get(this)
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

    val gainEvent = PlayerSkillExpGainEvent(this, skill, exp, !noMultiply)
    Bukkit.getPluginManager().callEvent(gainEvent)

    if (gainEvent.isCancelled) {
        return
    }

    this.giveExactSkillExperience(skill, gainEvent.amount)
}

fun Player.giveExactSkillExperience(skill: Skill, experience: Double) {
    val level = this.getSkillLevel(skill)

    val progress = this.getSkillProgress(skill) + experience

    if (progress >= skill.getExpForLevel(level + 1) && level + 1 <= skill.maxLevel) {
        val overshoot = progress - skill.getExpForLevel(level + 1)
        this.setSkillProgress(skill, 0.0)
        this.setSkillLevel(skill, level + 1)
        val levelUpEvent = PlayerSkillLevelUpEvent(this, skill, level + 1)
        Bukkit.getPluginManager().callEvent(levelUpEvent)
        this.giveExactSkillExperience(skill, overshoot)
    } else {
        this.setSkillProgress(skill, progress)
    }
}

fun OfflinePlayer.getSkillLevel(skill: Skill): Int {
    return this.profile.read(skill.dataKey)
}

fun OfflinePlayer.setSkillLevel(skill: Skill, level: Int) {
    this.profile.write(skill.dataKey, level)
}

fun OfflinePlayer.getSkillProgressToNextLevel(skill: Skill): Double {
    return this.getSkillProgress(skill) / this.getSkillProgressRequired(skill)
}

fun OfflinePlayer.getSkillProgressRequired(skill: Skill): Int {
    return skill.getExpForLevel(this.getSkillLevel(skill) + 1)
}

fun OfflinePlayer.getSkillProgress(skill: Skill): Double {
    val xp = this.profile.read(skill.dataXPKey)

    if (!xp.isFinite()) {
        this.profile.write(skill.dataXPKey, 1.0)
        return 1.0
    }

    return xp
}

fun OfflinePlayer.setSkillProgress(skill: Skill, xp: Double) {
    if (!xp.isFinite()) {
        return
    }

    this.profile.write(skill.dataXPKey, xp)
}

fun OfflinePlayer.getEffectLevel(effect: Effect): Int {
    return this.profile.read(effect.dataKey)
}

fun OfflinePlayer.setEffectLevel(effect: Effect, level: Int) {
    this.profile.write(effect.dataKey, level)
}

fun OfflinePlayer.getStatLevel(stat: Stat): Int {
    var base = this.getBaseStatLevel(stat)
    if (this is Player) {
        base += this.getBonusStatLevel(stat)
    }
    return base
}

fun Player.getBonusStatLevel(stat: Stat): Int {
    var added = 0.0
    for (modifier in this.getStatModifiers()) {
        if (modifier.stat == stat) {
            if (modifier.operation == ModifierOperation.ADD) {
                added += modifier.amount
            }
        }
    }
    val base = this.getBaseStatLevel(stat)
    var toMultiply = base + added

    for (modifier in this.getStatModifiers()) {
        if (modifier.stat == stat) {
            if (modifier.operation == ModifierOperation.MULTIPLY) {
                toMultiply *= modifier.amount
            }
        }
    }
    toMultiply -= base

    return toMultiply.toInt()
}

fun OfflinePlayer.getBaseStatLevel(stat: Stat): Int {
    return this.profile.read(stat.dataKey)
}

fun OfflinePlayer.setStatLevel(stat: Stat, level: Int) {
    this.profile.write(stat.dataKey, level)
    if (this is Player) {
        stat.updateStatLevel(this)
    }
}

private val gainSoundKey = PersistentDataKey(
    plugin.namespacedKeyFactory.create("gainSound"),
    PersistentDataKeyType.BOOLEAN,
    true
)

fun OfflinePlayer.hasGainSoundEnabled(): Boolean {
    return this.profile.read(gainSoundKey)
}

fun OfflinePlayer.setGainSoundEnabled(enabled: Boolean) {
    this.profile.write(gainSoundKey, enabled)
}

fun OfflinePlayer.toggleGainSoundEnabled() {
    this.profile.write(gainSoundKey, !hasGainSoundEnabled())
}

fun Entity.tryAsPlayer(): Player? {
    return when (this) {
        is Projectile -> this.shooter as? Player
        is Player -> this
        is Tameable -> this.owner as? Player
        else -> null
    }
}

fun OfflinePlayer.resetSkills() {
    for (stat in Stats.values()) {
        this.setStatLevel(stat, 0)
    }
    for (effect in Effects.values()) {
        this.setEffectLevel(effect, 0)
    }
    for (skill in Skills.values()) {
        this.setSkillLevel(skill, 0)
        this.setSkillProgress(skill, 0.0)
    }
}
