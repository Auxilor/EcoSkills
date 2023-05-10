package com.willfp.ecoskills.skills

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.toNiceString
import com.willfp.eco.util.toNumeral
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.api.getSkillLevel
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.libreforge.TriggerLevelUpSkill
import com.willfp.ecoskills.obj.Levellable
import com.willfp.ecoskills.stats.Stats
import com.willfp.ecoskills.util.InvalidConfigurationException
import com.willfp.ecoskills.util.LevelInjectable
import com.willfp.libreforge.EmptyProvidedHolder
import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.counters.Counters
import com.willfp.libreforge.effects.executors.impl.NormalExecutorFactory
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class Skill(
    id: String,
    config: Config,
    plugin: EcoSkillsPlugin
) : Levellable(id, config, plugin) {
    override val startLevel = 1

    private val xpKey = PersistentDataKey(
        plugin.createNamespacedKey("${id}_xp"),
        PersistentDataKeyType.DOUBLE,
        0.0
    )

    private val xpGainMethods = config.getSubsections("xp-gain-methods").mapNotNull {
        Counters.compile(it, ViolationContext(plugin, "Skill $id"))
    }

    private val xpFormula = config.getStringOrNull("xp-formula")

    private val requirements = config.getDoublesOrNull("xp-requirements")

    private val rewards = config.getSubsections("rewards").mapNotNull {
        val reward = Effects.getByID(it.getString("reward"))
            ?: Stats.getByID(it.getString("reward")) ?: return@mapNotNull null

        LevelUpReward(
            reward,
            it.getInt("level"),
            it.getIntOrNull("start-level"),
            it.getIntOrNull("end-level")
        )
    }

    private val levelUpEffects = com.willfp.libreforge.effects.Effects.compileChain(
        config.getSubsections("level-up-effects"),
        NormalExecutorFactory.create(),
        ViolationContext(plugin, "Skill $id level-up-effects")
    )

    init {
        if (xpFormula == null && requirements == null) {
            throw InvalidConfigurationException("Skill $id has no requirements or xp formula")
        }

        PlayerPlaceholder(plugin, "${id}_current_xp") {
            getSavedXP(it).toNiceString()
        }.register()

        PlayerPlaceholder(plugin, "${id}_required_xp") {
            getXPRequired(it.getSkillLevel(this)).toNiceString()
        }.register()

        PlayerPlaceholder(plugin, "${id}_percentage_progress") {
            val currentXP = getSavedXP(it)
            val requiredXP = getXPRequired(it.getSkillLevel(this))

            (currentXP / requiredXP * 100).toNiceString()
        }.register()
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
            return NumberUtils.evaluateExpression(
                xpFormula,
                placeholderContext(
                    injectable = LevelInjectable(level)
                )
            )
        }

        if (requirements != null) {
            return requirements.getOrNull(level - 1) ?: Double.POSITIVE_INFINITY
        }

        return Double.POSITIVE_INFINITY
    }

    internal fun handleLevelUp(player: OfflinePlayer, level: Int) {
        for (reward in rewards) {
            reward.giveTo(player, level)
        }

        if (player is Player) {
            // I don't really know a way to clean this up
            levelUpEffects?.trigger(
                DispatchedTrigger(
                    player,
                    TriggerLevelUpSkill,
                    TriggerData(
                        holder = EmptyProvidedHolder,
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
}

internal val OfflinePlayer.skills: SkillLevelMap
    get() = SkillLevelMap(this)
