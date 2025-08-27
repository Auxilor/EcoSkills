package com.willfp.ecoskills.skills

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.core.map.defaultMap
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.eco.util.evaluateExpression
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.toNiceString
import com.willfp.eco.util.toNumeral
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.Levellable
import com.willfp.ecoskills.api.getFormattedRequiredXP
import com.willfp.ecoskills.api.getSkillLevel
import com.willfp.ecoskills.api.getSkillProgress
import com.willfp.ecoskills.api.getSkillXP
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.gui.components.SkillIcon
import com.willfp.ecoskills.gui.menus.SkillLevelGUI
import com.willfp.ecoskills.libreforge.TriggerLevelUpSkill
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.stats.Stats
import com.willfp.ecoskills.util.InvalidConfigurationException
import com.willfp.ecoskills.util.LevelInjectable
import com.willfp.ecoskills.util.loadDescriptionPlaceholders
import com.willfp.libreforge.EmptyProvidedHolder
import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.counters.Counters
import com.willfp.libreforge.effects.executors.impl.NormalExecutorFactory
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class Skill(
    id: String,
    config: Config,
    plugin: EcoSkillsPlugin
) : Levellable(id, config, plugin) {
    private val xpKey = PersistentDataKey(
        plugin.createNamespacedKey("${id}_xp"),
        PersistentDataKeyType.DOUBLE,
        0.0
    )

    private val xpGainMethods = config.getSubsections("xp-gain-methods").mapNotNull {
        Counters.compile(it, ViolationContext(plugin, "Skill $id xp-gain-methods"))
    }

    val conditions = Conditions.compile(
        config.getSubsections("conditions"),
        ViolationContext(plugin, "Skill $id conditions")
    )

    private val xpFormula = config.getStringOrNull("xp-formula")

    private val requirements = config.getDoublesOrNull("xp-requirements")

    val maxLevel = config.getIntOrNull("max-level") ?: requirements?.size ?: Int.MAX_VALUE

    private val rewards = config.getSubsections("rewards").mapNotNull {
        val reward = Effects.getByID(it.getString("reward"))
            ?: Stats.getByID(it.getString("reward")) ?: return@mapNotNull null

        LevelUpReward(
            reward,
            it.getInt("levels"),
            it.getIntOrNull("start-level"),
            it.getIntOrNull("end-level"),
            it.getIntOrNull("every")
        )
    }

    private val levelUpEffects = com.willfp.libreforge.effects.Effects.compileChain(
        config.getSubsections("level-up-effects"),
        NormalExecutorFactory.create(),
        ViolationContext(plugin, "Skill $id level-up-effects")
    )

    val levelGUI = SkillLevelGUI(plugin, this)

    val icon = SkillIcon(this, config.getSubsection("gui"), plugin)

    val isHiddenBeforeLevel1 = config.getBool("hide-before-level-1")

    init {
        if (xpFormula == null && requirements == null) {
            throw InvalidConfigurationException("Skill $id has no requirements or xp formula")
        }

        PlayerPlaceholder(plugin, "${id}_current_xp") {
            getSavedXP(it).toNiceString()
        }.register()

        PlayerPlaceholder(plugin, "${id}_required_xp") {
            getFormattedXPRequired(it.getSkillLevel(this))
        }.register()

        PlayerPlaceholder(plugin, "${id}_percentage_progress") {
            (it.getSkillProgress(this) * 100).toNiceString()
        }.register()
    }

    override fun onRegister() {
        val accumulator = SkillXPAccumulator(plugin, this)

        for (counter in xpGainMethods) {
            counter.bind(accumulator)
        }
    }

    override fun onRemove() {
        for (counter in xpGainMethods) {
            counter.unbind()
        }
    }

    /**
     * Get the XP required to reach the next level, if currently at [level].
     */
    fun getXPRequired(level: Int): Double {
        if (xpFormula != null) {
            return evaluateExpression(
                xpFormula,
                placeholderContext(
                    injectable = LevelInjectable(level)
                )
            )
        }

        if (requirements != null) {
            return requirements.getOrNull(level) ?: Double.POSITIVE_INFINITY
        }

        return Double.POSITIVE_INFINITY
    }

    fun getFormattedXPRequired(level: Int): String {
        val required = getXPRequired(level)
        return if (required.isInfinite()) {
            plugin.langYml.getFormattedString("infinity")
        } else {
            required.toNiceString()
        }
    }

    /**
     * Add skill placeholders into [strings], to be shown to a [player].
     */
    fun addPlaceholdersInto(
        strings: List<String>,
        player: Player,
        level: Int = player.getSkillLevel(this)
    ): List<String> {
        val skill = this // I just hate the @ notation kotlin uses
        fun String.addPlaceholders() = this
            .replace("%percentage_progress%", (player.getSkillProgress(skill) * 100).toNiceString())
            .replace("%current_xp%", player.getSkillXP(skill).toNiceString())
            .replace("%required_xp%", player.getFormattedRequiredXP(skill))
            .replace("%description%", skill.getDescription(level))
            .replace("%skill%", skill.name)
            .replace("%rank%", skill.getPosition(player.uniqueId)?.toString() ?: plugin.langYml.getString("top.empty-position"))
            .let { addPlaceholdersInto(it, level) }
            .injectRewardPlaceholders(level)

        // Replace placeholders in the strings with their actual values.
        val withPlaceholders = strings.map { it.addPlaceholders() }

        // Replace multi-line placeholders.
        val processed = withPlaceholders.flatMap { s ->
            val margin = s.length - s.trimStart().length

            if (s.contains("%rewards%")) {
                getRewardMessages(level, player)
                    .addMargin(margin)
            } else if (s.contains("%gui_lore%")) {
                config.getStrings("gui.lore")
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

    // Total hack, but that's how I did it before and I *really* don't want to change it.
    private fun String.injectRewardPlaceholders(level: Int): String {
        var processed = this

        // Fixes visual bug with repeated rewards
        val levels = defaultMap<Levellable, Int>(0)
        for (reward in rewards) {
            levels[reward.reward] += reward.getCumulativeLevels(level)
        }

        for ((reward, lvl) in levels) {
            processed = reward.addPlaceholdersInto(processed, lvl)
        }

        return processed
    }

    private fun List<String>.addMargin(margin: Int): List<String> {
        return this.map { s -> " ".repeat(margin) + s }
    }

    /**
     * Get the reward messages for a certain [level].
     */
    private fun getRewardMessages(
        level: Int,
        player: Player
    ): List<String> {
        val context = placeholderContext(
            injectable = LevelInjectable(level),
            player = player
        )

        // Determine the highest level of messages from the config that is not greater than the provided level.
        val highestConfiguredLevel = config.getSubsection("reward-messages")
            .getKeys(false)
            .mapNotNull { it.toIntOrNull() } // Convert strings to Int, ignoring any that cannot be converted
            .filter { it <= level } // Only consider levels not greater than the provided level
            .maxOrNull() ?: 1 // Get the maximum level, default to 1 if no suitable level was found

        val messages = config.getStrings("reward-messages.$highestConfiguredLevel").toMutableList()

        for (placeholder in loadDescriptionPlaceholders(config)) {
            val id = placeholder.id
            val value = evaluateExpression(placeholder.expr, context)

            messages.replaceAll { s -> s.replace("%$id%", value.toNiceString()) }
        }

        return messages
    }

    fun giveRewards(player: OfflinePlayer, level: Int) {
        for (reward in rewards) {
            reward.giveTo(player, level)
        }
    }

    internal fun handleLevelUp(player: OfflinePlayer, level: Int) {
        giveRewards(player, level)

        if (player is Player) {
            // I don't really know a way to clean this up
            levelUpEffects?.trigger(
                DispatchedTrigger(
                    player.toDispatcher(),
                    TriggerLevelUpSkill,
                    TriggerData(
                        player = player
                    )
                ).apply {
                    addPlaceholder(NamedValue("level", level))
                    addPlaceholder(NamedValue("level_numeral", level.toNumeral()))
                }
            )
        }
    }

    internal fun getSavedXP(player: OfflinePlayer): Double = player.profile.read(xpKey)
    internal fun setSavedXP(player: OfflinePlayer, xp: Double) = player.profile.write(xpKey, xp)

    override fun equals(other: Any?): Boolean {
        return other is Skill && other.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

internal val OfflinePlayer.skills: SkillLevelMap
    get() = SkillLevelMap(this)

val Player.isInDisabledWorld: Boolean
    get() = plugin.configYml.getStrings("disabled-in-worlds").containsIgnoreCase(world.name)
