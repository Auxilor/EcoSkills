package com.willfp.ecoskills.skills

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.eco.util.evaluateExpression
import com.willfp.eco.util.toNiceString
import com.willfp.eco.util.toNumeral
import com.willfp.ecoskills.Levellable
import com.willfp.ecoskills.Placeholders
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.gui.components.SkillIcon
import com.willfp.ecoskills.gui.menus.SkillLevelGUI
import com.willfp.ecoskills.libreforge.TriggerLevelUpSkill
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.stats.Stats
import com.willfp.ecoskills.util.InvalidConfigurationException
import com.willfp.ecoskills.util.LevelInjectable
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
    config: Config
) : Levellable(id, config) {
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

    internal val rewards = config.getSubsections("rewards").mapNotNull {
        val reward = Effects.getByID(it.getString("reward"))
            ?: Stats.getByID(it.getString("reward")) ?: return@mapNotNull null

        LevelUpReward(
            reward,
            it.getInt("levels"),
            it.getIntOrNull("start-level"),
            it.getIntOrNull("end-level"),
            it.getIntOrNull("every"),
            it.getIntsOrNull("level-list")
        )
    }

    private val levelUpEffects = com.willfp.libreforge.effects.Effects.compileChain(
        config.getSubsections("level-up-effects"),
        NormalExecutorFactory.create(),
        ViolationContext(plugin, "Skill $id level-up-effects")
    )

    val levelGUI = SkillLevelGUI(this)

    val icon = SkillIcon(this, config.getSubsection("gui"))

    val isHiddenBeforeLevel1 = config.getBool("hide-before-level-1")

    init {
        if (xpFormula == null && requirements == null) {
            throw InvalidConfigurationException("Skill $id has no requirements or xp formula")
        }

        Placeholders.applyExternalSkillPlaceholders(this)
    }

    override fun onRegister() {
        val accumulator = SkillXPAccumulator(this)

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
            // Level 0 would make most formulas return 0; use 1 to get XP required to reach level 1.
            val formulaLevel = if (level == 0) 1 else level
            return evaluateExpression(
                xpFormula,
                placeholderContext(
                    injectable = LevelInjectable(formulaLevel)
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
                    addPlaceholder(NamedValue("previous_level", level - 1))
                    addPlaceholder(NamedValue("previous_level_numeral", (level - 1).toNumeral()))
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
