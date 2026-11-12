package com.willfp.ecoskills

import com.willfp.ecoskills.actionbar.ActionBarHandler
import com.willfp.ecoskills.actionbar.HealthScaleDisabler
import com.willfp.ecoskills.commands.ecoSkillsCommand
import com.willfp.ecoskills.commands.skillsCommand
import com.willfp.ecoskills.commands.statsCommand
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.libreforge.ConditionAboveMagic
import com.willfp.ecoskills.libreforge.ConditionBelowMagic
import com.willfp.ecoskills.libreforge.ConditionHasSkillLevel
import com.willfp.ecoskills.libreforge.ConditionStatAbove
import com.willfp.ecoskills.libreforge.ConditionStatBelow
import com.willfp.ecoskills.libreforge.ConditionStatEquals
import com.willfp.ecoskills.libreforge.EffectAddStat
import com.willfp.ecoskills.libreforge.EffectAddStatTemporarily
import com.willfp.ecoskills.libreforge.EffectGiveMagic
import com.willfp.ecoskills.libreforge.EffectGiveSkillXp
import com.willfp.ecoskills.libreforge.EffectGiveSkillXpNaturally
import com.willfp.ecoskills.libreforge.EffectMagicRegenMultiplier
import com.willfp.ecoskills.libreforge.EffectMakeSkillCrit
import com.willfp.ecoskills.libreforge.EffectMultiplyAllStats
import com.willfp.ecoskills.libreforge.EffectMultiplyMagic
import com.willfp.ecoskills.libreforge.EffectMultiplyStat
import com.willfp.ecoskills.libreforge.EffectMultiplyStatTemporarily
import com.willfp.ecoskills.libreforge.EffectSkillXpMultiplier
import com.willfp.ecoskills.libreforge.FilterMagicType
import com.willfp.ecoskills.libreforge.FilterSkill
import com.willfp.ecoskills.libreforge.FilterSkillCrit
import com.willfp.ecoskills.libreforge.TriggerGainSkillXp
import com.willfp.ecoskills.libreforge.TriggerLevelUpSkill
import com.willfp.ecoskills.libreforge.TriggerRegenMagic
import com.willfp.ecoskills.magic.MagicHandler
import com.willfp.ecoskills.magic.MagicListener
import com.willfp.ecoskills.magic.MagicTypes
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.skills.display.DamageIndicatorListener
import com.willfp.ecoskills.skills.display.GainXPDisplay
import com.willfp.ecoskills.skills.display.LevelUpDisplay
import com.willfp.ecoskills.skills.display.TemporaryBossBarHandler
import com.willfp.ecoskills.skills.isInDisabledWorld
import com.willfp.ecoskills.stats.StatModifierListener
import com.willfp.ecoskills.stats.Stats
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import wiki.ecohub.config.category.NativeConfigCategory
import wiki.ecohub.core.command.EcoCommand
import wiki.ecohub.core.plugin.EcoPlugin
import wiki.ecohub.data.playerbase.leaderboard.Leaderboards
import wiki.ecohub.data.playerbase.leaderboard.registerCategoryTopPlaceholders
import wiki.ecohub.provider.metrics.EcoMetricsChart
import wiki.ecohub.rules.condition.Conditions
import wiki.ecohub.rules.filter.Filters
import wiki.ecohub.rules.holder.SimpleProvidedHolder
import wiki.ecohub.rules.holder.registerSpecificHolderProvider
import wiki.ecohub.rules.trigger.Triggers
import wiki.ecohub.ui.text.formatEco

internal lateinit var plugin: EcoSkillsPlugin
    private set

class EcoSkillsPlugin : EcoPlugin() {
    init {
        plugin = this
    }

    override fun loadConfigCategories(): List<NativeConfigCategory> {
        return listOf(
            MagicTypes,
            Stats,
            Effects,
            Skills
        )
    }

    override fun handleEnable() {
        registerSpecificHolderProvider<Player> { player ->
            if (player.isInDisabledWorld) emptyList() else
                (Effects.values() union Stats.values())
                    .mapNotNull { it.getLevelHolder(it.getActualLevel(player)) }
                    .map { SimpleProvidedHolder(it) }
        }

        wiki.ecohub.rules.effect.Effects.register(EffectAddStat)
        wiki.ecohub.rules.effect.Effects.register(EffectMultiplyStat)
        wiki.ecohub.rules.effect.Effects.register(EffectSkillXpMultiplier)
        wiki.ecohub.rules.effect.Effects.register(EffectGiveSkillXp)
        wiki.ecohub.rules.effect.Effects.register(EffectMultiplyAllStats)
        wiki.ecohub.rules.effect.Effects.register(EffectMakeSkillCrit)
        wiki.ecohub.rules.effect.Effects.register(EffectGiveMagic)
        wiki.ecohub.rules.effect.Effects.register(EffectMultiplyMagic)
        wiki.ecohub.rules.effect.Effects.register(EffectGiveSkillXpNaturally)
        wiki.ecohub.rules.effect.Effects.register(EffectAddStatTemporarily)
        wiki.ecohub.rules.effect.Effects.register(EffectMultiplyStatTemporarily)
        wiki.ecohub.rules.effect.Effects.register(EffectMagicRegenMultiplier)
        Conditions.register(ConditionStatAbove)
        Conditions.register(ConditionStatBelow)
        Conditions.register(ConditionStatEquals)
        Conditions.register(ConditionHasSkillLevel)
        Conditions.register(ConditionBelowMagic)
        Conditions.register(ConditionAboveMagic)
        Triggers.register(TriggerGainSkillXp)
        Triggers.register(TriggerLevelUpSkill)
        Triggers.register(TriggerRegenMagic)
        Filters.register(FilterSkill)
        Filters.register(FilterSkillCrit)
        Filters.register(FilterMagicType)

        // Registered once for every skill at once: the lookup resolves the ID when the
        // placeholder is read, so a skill added or renamed in a config needs nothing here.
        // A disabled leaderboard is null, so its placeholders stay unresolved until enabled.
        registerCategoryTopPlaceholders(
            this,
            this.langYml.getString("top.empty-position").formatEco(),
            listOf("level", "amount")
        ) { Skills.getByID(it)?.leaderboard }
        Skills.registerPlaceholders()
    }

    override fun handleReload() {
        // Config categories are loaded by libreforge's onReload(START) tasks, which run before
        // this handler, so every skill exists by now and re-registering here is what keeps
        // leaderboards for removed skills from lingering.
        Leaderboards.unregisterAll(this)

        for (skill in Skills.values()) {
            skill.registerLeaderboard()
        }

        Skills.registerLeaderboard()

        ActionBarHandler.reload()

        TemporaryBossBarHandler.startTicking()
        MagicHandler.startTicking()
        GainXPDisplay.startTickingSounds()
        LevelUpDisplay.startTickingSounds()
    }

    override fun loadPluginCommands(): List<EcoCommand> {
        return listOf(
            ecoSkillsCommand(),
            skillsCommand(),
            statsCommand()
        )
    }

    override fun loadListeners(): List<Listener> {
        return listOf(
            LevelUpDisplay,
            GainXPDisplay,
            DamageIndicatorListener,
            MagicListener,
            HealthScaleDisabler,
            StatModifierListener
        )
    }

    override fun getCustomCharts() = listOf(
        EcoMetricsChart.SingleLine("total_skills") { Skills.values().size },
        EcoMetricsChart.SingleLine("total_stats") { Stats.values().size },
        EcoMetricsChart.SingleLine("total_skill_effects") { Effects.values().size },
        EcoMetricsChart.SingleLine("total_magic_types") { MagicTypes.values().size },
        EcoMetricsChart.SimplePie("leaderboard_enabled") {
            if (configYml.getBool("leaderboard.enabled")) "enabled" else "disabled"
        },
        EcoMetricsChart.SimplePie("use_local_storage") {
            if (configYml.getBool("use-local-storage")) "local" else "shared"
        },
        EcoMetricsChart.SimplePie("damage_indicators_enabled") {
            if (configYml.getBool("damage-indicators.enabled")) "enabled" else "disabled"
        },
        EcoMetricsChart.SimplePie("persistent_action_bar") {
            if (configYml.getBool("persistent-action-bar.enabled")) "enabled" else "disabled"
        }
    )
}
