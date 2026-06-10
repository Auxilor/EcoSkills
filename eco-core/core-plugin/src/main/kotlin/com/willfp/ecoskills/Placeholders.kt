package com.willfp.ecoskills

import com.willfp.eco.core.map.defaultMap
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.eco.core.placeholder.PlaceholderInjectable
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.core.placeholder.PlayerStaticPlaceholder
import com.willfp.eco.core.placeholder.PlayerlessPlaceholder
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.util.evaluateExpression
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.toNiceString
import com.willfp.eco.util.toNumeral
import com.willfp.ecoskills.api.averageSkillLevel
import com.willfp.ecoskills.api.getBaseStatLevel
import com.willfp.ecoskills.api.getBonusStatLevel
import com.willfp.ecoskills.api.getFormattedRequiredXP
import com.willfp.ecoskills.api.getSkillLevel
import com.willfp.ecoskills.api.getSkillProgress
import com.willfp.ecoskills.api.getSkillXP
import com.willfp.ecoskills.api.getStatLevel
import com.willfp.ecoskills.api.totalSkillLevel
import com.willfp.ecoskills.magic.MagicType
import com.willfp.ecoskills.magic.magic
import com.willfp.ecoskills.skills.EcoSkillsSkillTopPlaceholder
import com.willfp.ecoskills.skills.EcoSkillsTopPlaceholder
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.skills.SkillsLeaderboard.getPosition
import com.willfp.ecoskills.stats.Stat
import com.willfp.ecoskills.util.LevelInjectable
import com.willfp.ecoskills.util.loadDescriptionPlaceholders
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

/**
 * Holds every placeholder EcoSkills exposes.
 *
 * applyExternal functions register PlaceholderAPI placeholders that are accessible
 * from outside the plugin.
 *
 * applyInternal functions provide the injectable placeholders used inside the plugin's
 * own configs and contexts, such as descriptions and the action bar.
 */
object Placeholders {
    private val LEVEL_OFFSET_REGEX = Regex("%level_(-?\\d+)(_numeral)?%")

    private fun rankOrEmpty(position: Int?): String =
        position?.toString() ?: plugin.langYml.getString("top.empty-position")

    /**
     * Registers the placeholders shared by every [Levellable].
     */
    fun applyExternalLevellablePlaceholders(levellable: Levellable) {
        PlayerPlaceholder(plugin, levellable.id) {
            levellable.getActualLevel(it).toString()
        }.register()

        PlayerPlaceholder(plugin, "${levellable.id}_numeral") {
            levellable.getActualLevel(it).toNumeral()
        }.register()

        PlayerlessPlaceholder(plugin, "${levellable.id}_name") {
            levellable.name
        }.register()

        PlayerPlaceholder(plugin, "${levellable.id}_description") {
            levellable.getDescription(levellable.getActualLevel(it))
        }.register()

        PlayerPlaceholder(plugin, "${levellable.id}_leaderboard_rank") { player ->
            rankOrEmpty(getPosition(levellable, player.uniqueId))
        }.register()
    }

    /**
     * Registers the placeholders specific to a [Skill].
     */
    fun applyExternalSkillPlaceholders(skill: Skill) {
        PlayerPlaceholder(plugin, "${skill.id}_current_xp") {
            skill.getSavedXP(it).toNiceString()
        }.register()

        PlayerPlaceholder(plugin, "${skill.id}_required_xp") {
            skill.getFormattedXPRequired(it.getSkillLevel(skill))
        }.register()

        PlayerPlaceholder(plugin, "${skill.id}_percentage_progress") {
            (it.getSkillProgress(skill) * 100).toNiceString()
        }.register()
    }

    /**
     * Registers the placeholders specific to a [Stat].
     */
    fun applyExternalStatPlaceholders(stat: Stat) {
        PlayerPlaceholder(plugin, "${stat.id}_base") {
            it.getBaseStatLevel(stat).toNiceString()
        }.register()

        PlayerPlaceholder(plugin, "${stat.id}_bonus") {
            val bonus = it.getBonusStatLevel(stat)
            when {
                bonus > 0 -> "+${bonus}"
                bonus < 0 -> "$bonus"
                else -> ""
            }
        }.register()
    }

    /**
     * Registers the placeholders specific to a [MagicType].
     */
    fun applyExternalMagicPlaceholders(magicType: MagicType) {
        PlayerlessPlaceholder(plugin, "${magicType.id}_name") {
            magicType.name
        }.register()

        PlayerPlaceholder(plugin, magicType.id) {
            it.magic[magicType].toNiceString()
        }.register()

        PlayerPlaceholder(plugin, "${magicType.id}_numeral") {
            it.magic[magicType].toNumeral()
        }.register()

        PlayerPlaceholder(plugin, "${magicType.id}_limit") {
            magicType.getLimit(it).toNiceString()
        }.register()
    }

    /**
     * Registers the global skill placeholders and the leaderboard placeholders.
     */
    fun applyExternalSkillsPlaceholders() {
        PlayerPlaceholder(plugin, "leaderboard_rank") { player ->
            rankOrEmpty(Skills.getPosition(player.uniqueId))
        }.register()

        if (plugin.configYml.getBool("leaderboard.enabled")) {
            EcoSkillsTopPlaceholder.register()
            EcoSkillsSkillTopPlaceholder.register()
        }
    }

    /**
     * Registers the global stat placeholders.
     */
    fun applyExternalStatsPlaceholders() {
        PlayerPlaceholder(plugin, "total_skill_level") {
            it.totalSkillLevel.toNiceString()
        }.register()

        PlayerPlaceholder(plugin, "average_skill_level") {
            it.averageSkillLevel.toNiceString()
        }.register()
    }

    /**
     * Injects the level placeholder into a [Levellable]'s config.
     */
    fun applyInternalLevellablePlaceholders(levellable: Levellable) {
        levellable.config.injectPlaceholders(
            PlayerStaticPlaceholder("level") {
                levellable.getActualLevel(it).toString()
            }
        )
    }

    /**
     * Replaces the placeholders shared by every [Levellable] inside [string], resolved
     * against the given [level], including the relative offset forms level_X and level_X_numeral.
     */
    fun applyInternalLevellablePlaceholders(levellable: Levellable, string: String, level: Int): String {
        var result = string
            .replace("%ecoskills_${levellable.id}_numeral%", level.toNumeral())
            .replace("%ecoskills_${levellable.id}_description%", levellable.getDescription(level))
            .replace("%ecoskills_${levellable.id}%", level.toString())
            .replace("%level%", level.toString())
            .replace("%level_numeral%", level.toNumeral())
            .replace("%previous_level%", (level - 1).toString())
            .replace("%previous_level_numeral%", (level - 1).toNumeral())

        result = LEVEL_OFFSET_REGEX.replace(result) { match ->
            val offset = match.groupValues[1].toIntOrNull() ?: return@replace match.value
            val isNumeral = match.groupValues[2].isNotEmpty()
            val newLevel = level + offset

            if (isNumeral) newLevel.toNumeral() else newLevel.toString()
        }

        return result
    }

    /**
     * Replaces the skill placeholders inside [strings], to be shown to a [player].
     */
    fun applyInternalSkillPlaceholders(
        skill: Skill,
        strings: List<String>,
        player: Player,
        level: Int = player.getSkillLevel(skill)
    ): List<String> {
        fun String.addPlaceholders() = this
            .replace("%percentage_progress%", (player.getSkillProgress(skill) * 100).toNiceString())
            .replace("%current_xp%", player.getSkillXP(skill).toNiceString())
            .replace("%required_xp%", player.getFormattedRequiredXP(skill))
            .replace("%description%", skill.getDescription(level))
            .replace("%skill%", skill.name)
            .replace("%rank%", rankOrEmpty(getPosition(skill, player.uniqueId)))
            .let { applyInternalLevellablePlaceholders(skill, it, level) }
            .injectRewardPlaceholders(skill, level)

        val withPlaceholders = strings.map { it.addPlaceholders() }

        val processed = withPlaceholders.flatMap { s ->
            val margin = s.length - s.trimStart().length

            if (s.contains("%rewards%")) {
                getSkillRewardMessages(skill, level, player)
                    .addMargin(margin)
            } else if (s.contains("%gui_lore%")) {
                skill.config.getStrings("gui.lore")
                    .addMargin(margin)
            } else {
                listOf(s)
            }
        }.map { it.addPlaceholders() }

        return processed.formatEco(
            placeholderContext(
                player = player
            )
        )
    }

    /**
     * Replaces the stat placeholders inside [strings], to be shown to a [player].
     */
    fun applyInternalStatPlaceholders(
        stat: Stat,
        strings: List<String>,
        player: Player,
        level: Int = player.getStatLevel(stat)
    ): List<String> {
        return strings.map { s ->
            s.replace("%description%", stat.getDescription(level))
                .replace("%stat%", stat.name)
                .let { applyInternalLevellablePlaceholders(stat, it, level) }
        }.formatEco(player)
    }

    private fun String.injectRewardPlaceholders(skill: Skill, level: Int): String {
        var processed = this

        val levels = defaultMap<Levellable, Int>(0)
        for (reward in skill.rewards) {
            levels[reward.reward] += reward.getCumulativeLevels(level)
        }

        for ((reward, lvl) in levels) {
            processed = applyInternalLevellablePlaceholders(reward, processed, lvl)
        }

        return processed
    }

    private fun List<String>.addMargin(margin: Int): List<String> {
        return this.map { s -> " ".repeat(margin) + s }
    }

    /**
     * Builds the reward messages for the given [level].
     */
    private fun getSkillRewardMessages(
        skill: Skill,
        level: Int,
        player: Player
    ): List<String> {
        val context = placeholderContext(
            injectable = LevelInjectable(level),
            player = player
        )

        val highestConfiguredLevel = skill.config.getSubsection("reward-messages")
            .getKeys(false)
            .mapNotNull { it.toIntOrNull() }
            .filter { it <= level }
            .maxOrNull() ?: 1

        val messages = skill.config.getStrings("reward-messages.$highestConfiguredLevel").toMutableList()

        for (placeholder in loadDescriptionPlaceholders(skill.config)) {
            val id = placeholder.id
            val value = evaluateExpression(placeholder.expr, context)

            messages.replaceAll { s -> s.replace("%$id%", value.toNiceString()) }
        }

        return messages
    }

    /**
     * The health and max_health injectable used by the persistent action bar.
     */
    fun applyInternalActionBarPlaceholders(): PlaceholderInjectable = PlayerHealthInjectable

    private object PlayerHealthInjectable : PlaceholderInjectable {
        private val injections = listOf(
            PlayerStaticPlaceholder(
                "health"
            ) { it.health.toInt().toString() },
            PlayerStaticPlaceholder(
                "max_health"
            ) { it.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value?.toInt()?.toString() ?: "20" },
        )

        override fun getPlaceholderInjections(): List<InjectablePlaceholder> {
            return injections
        }

        override fun addInjectablePlaceholder(p0: Iterable<InjectablePlaceholder>) {
            return
        }

        override fun clearInjectedPlaceholders() {
            return
        }
    }
}