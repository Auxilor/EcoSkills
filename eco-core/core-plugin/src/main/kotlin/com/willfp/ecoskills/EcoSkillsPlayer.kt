package com.willfp.ecoskills

import com.willfp.eco.core.integrations.placeholder.PlaceholderEntry
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.api.PlayerSkillExpGainEvent
import com.willfp.ecoskills.api.PlayerSkillLevelUpEvent
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stat
import com.willfp.ecoskills.stats.Stats
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import java.util.*


object PlayerHelper {
    init {
        PlaceholderEntry(
            "average_skill_level",
            { player -> NumberUtils.format(player.getAverageSkillLevel()) },
            true
        ).register()
        PlaceholderEntry(
            "total_skill_level",
            { player -> player.getTotalSkillLevel().toString() },
            true
        ).register()
    }

    val expMultiplierCache = HashMap<UUID, Double>()

    val plugin: EcoSkillsPlugin = EcoSkillsPlugin.getInstance()
}

fun Player.getSkillExperienceMultiplier(): Double {
    if (PlayerHelper.expMultiplierCache.containsKey(this.uniqueId)) {
        return PlayerHelper.expMultiplierCache[this.uniqueId]!!
    }
    PlayerHelper.expMultiplierCache[this.uniqueId] = cacheSkillExperienceMultiplier()
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
            return (permission.substring(permission.lastIndexOf(".") + 1).toDoubleOrNull() ?: 100.0 / 100) + 1
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

fun Player.giveSkillExperience(skill: Skill, experience: Double, isOvershoot: Boolean = false) {
    val exp = if (isOvershoot) experience else experience * this.getSkillExperienceMultiplier()

    val gainEvent = PlayerSkillExpGainEvent(this, skill, exp)
    Bukkit.getPluginManager().callEvent(gainEvent)

    if (gainEvent.isCancelled) {
        return
    }

    val level = this.getSkillLevel(skill)

    this.setSkillProgress(skill, this.getSkillProgress(skill) + exp)

    if (this.getSkillProgress(skill) >= skill.getExpForLevel(level + 1) && level + 1 <= skill.maxLevel) {
        val overshoot = this.getSkillProgress(skill) - skill.getExpForLevel(level + 1);
        this.setSkillProgress(skill, 0.0)
        this.setSkillLevel(skill, level + 1)
        val levelUpEvent = PlayerSkillLevelUpEvent(this, skill, level + 1)
        Bukkit.getPluginManager().callEvent(levelUpEvent)
        this.giveSkillExperience(skill, overshoot, true)
    }
}

fun OfflinePlayer.getSkillLevel(skill: Skill): Int {
    return PlayerHelper.plugin.dataYml.getInt("player.${this.uniqueId}.${skill.id}", 0)
}

fun Player.setSkillLevel(skill: Skill, level: Int) {
    PlayerHelper.plugin.dataYml.set("player.${this.uniqueId}.${skill.id}", level)
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

fun OfflinePlayer.getEffectLevel(effect: Effect): Int {
    return PlayerHelper.plugin.dataYml.getInt("player.${this.uniqueId}.${effect.id}", 0)
}

fun Player.setEffectLevel(effect: Effect, level: Int) {
    PlayerHelper.plugin.dataYml.set("player.${this.uniqueId}.${effect.id}", level)
}

fun OfflinePlayer.getStatLevel(stat: Stat): Int {
    return PlayerHelper.plugin.dataYml.getInt("player.${this.uniqueId}.${stat.id}", 0)
}

fun Player.setStatLevel(stat: Stat, level: Int) {
    PlayerHelper.plugin.dataYml.set("player.${this.uniqueId}.${stat.id}", level)
    stat.updateStatLevel(this)
}

fun Player.convertPersistentToYml() {
    for (effect in Effects.values()) {
        PlayerHelper.plugin.dataYml.set("player.${this.uniqueId}.${effect.id}", this.getEffectLevel(effect))
    }
    for (stat in Stats.values()) {
        PlayerHelper.plugin.dataYml.set("player.${this.uniqueId}.${stat.id}", this.getStatLevel(stat))
    }
    for (skill in Skills.values()) {
        PlayerHelper.plugin.dataYml.set("player.${this.uniqueId}.${skill.id}", this.getSkillLevel(skill))
    }
}