@file:JvmName("EcoSkillsAPI")

package com.willfp.ecoskills.api

import com.willfp.ecoskills.api.modifiers.StatModifier
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.effects.effects
import com.willfp.ecoskills.magic.MagicType
import com.willfp.ecoskills.magic.magic
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.skills.SkillLevel
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.skills.skills
import com.willfp.ecoskills.stats.Stat
import com.willfp.ecoskills.stats.Stats
import com.willfp.ecoskills.stats.statModifiers
import com.willfp.ecoskills.stats.stats
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.UUID

/*

Skills

 */

fun OfflinePlayer.resetSkills() {
    for (skill in Skills.values()) {
        this.skills.reset(skill)
    }
    for (stat in Stats.values()) {
        this.stats.reset(stat)
    }
    for (effect in Effects.values()) {
        this.effects.reset(effect)
    }
}

fun OfflinePlayer.resetSkill(skill: Skill) =
    this.skills.reset(skill)

fun OfflinePlayer.getSkillXP(skill: Skill): Double =
    this.skills[skill].xp

fun OfflinePlayer.gainSkillXP(skill: Skill, xp: Double): Unit =
    this.skills.gainXP(skill, xp)

fun OfflinePlayer.giveSkillXP(skill: Skill, xp: Double): Unit =
    this.skills.giveXP(skill, xp)

fun OfflinePlayer.getRequiredXP(skill: Skill) =
    skill.getXPRequired(this.getSkillLevel(skill))

fun OfflinePlayer.getFormattedRequiredXP(skill: Skill) =
    skill.getFormattedXPRequired(this.getSkillLevel(skill))

fun OfflinePlayer.getSkillProgress(skill: Skill): Double {
    val currentXP = getSkillXP(skill)
    val requiredXP = getRequiredXP(skill)

    return currentXP / requiredXP
}

fun OfflinePlayer.getSkillLevel(skill: Skill): Int =
    this.skills[skill].level

fun OfflinePlayer.setSkillLevel(skill: Skill, level: Int) =
    this.skills.set(skill, SkillLevel(level, 0.0))

val OfflinePlayer.totalSkillLevel: Int
    get() = Skills.values().sumOf { this.getSkillLevel(it) }

val OfflinePlayer.averageSkillLevel: Double
    get() = this.totalSkillLevel.toDouble() / Skills.values().size


/*

Stats

 */

fun OfflinePlayer.getBaseStatLevel(stat: Stat): Int =
    this.stats[stat]

fun OfflinePlayer.setBaseStatLevel(stat: Stat, value: Int) =
    this.stats.set(stat, value)

fun OfflinePlayer.giveBaseStatLevel(stat: Stat, amount: Int) {
    this.stats[stat] += amount
}

fun OfflinePlayer.getStatLevel(stat: Stat): Int =
    if (this is Player) this.statModifiers.getModifiedValue(stat) else this.getBaseStatLevel(stat)

val OfflinePlayer.statModifiers: List<StatModifier>
    get() = if (this is Player) this.statModifiers.getModifiers() else emptyList()

fun OfflinePlayer.getStatModifiers(stat: Stat): List<StatModifier> =
    if (this is Player) this.statModifiers.getModifiers(stat) else emptyList()

fun OfflinePlayer.getBonusStatLevel(stat: Stat): Int =
    if (this is Player) this.statModifiers.getBonusStatLevel(stat) else 0

fun Player.addStatModifier(statModifier: StatModifier): Unit =
    this.statModifiers.add(statModifier)

fun Player.removeStatModifier(uuid: UUID): StatModifier? =
    this.statModifiers.remove(uuid)

fun Player.getStatModifier(uuid: UUID): StatModifier? =
    this.statModifiers.getModifier(uuid)

/*

Effects

 */

fun OfflinePlayer.getEffectLevel(effect: Effect): Int =
    this.effects[effect]

/*

Mana

 */

fun Player.getMagic(type: MagicType): Int =
    this.magic[type]

fun Player.setMagic(type: MagicType, amount: Int): Unit =
    this.magic.set(type, amount)

fun Player.getMaxMagic(type: MagicType): Int =
    type.getLimit(this)
