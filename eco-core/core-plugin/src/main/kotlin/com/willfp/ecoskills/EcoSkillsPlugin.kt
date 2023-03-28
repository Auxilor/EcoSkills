@file:Suppress("DEPRECATION")

package com.willfp.ecoskills

import com.willfp.eco.core.AbstractPacketAdapter
import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.integrations.IntegrationLoader
import com.willfp.ecoskills.actionbar.ActionBarClearOnGamemode
import com.willfp.ecoskills.actionbar.ActionBarCompatChatMessage
import com.willfp.ecoskills.actionbar.ActionBarCompatSetActionBar
import com.willfp.ecoskills.actionbar.ActionBarUtils.startRunnable
import com.willfp.ecoskills.actionbar.HealthScaleListener
import com.willfp.ecoskills.commands.CommandEcoSkills
import com.willfp.ecoskills.commands.CommandSkills
import com.willfp.ecoskills.config.EffectsYml
import com.willfp.ecoskills.data.DataListener
import com.willfp.ecoskills.data.LeaderboardHandler
import com.willfp.ecoskills.effects.CustomEffects
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.effects.customEffects
import com.willfp.ecoskills.integrations.EcoEnchantsEnchantingLeveller
import com.willfp.ecoskills.integrations.enchantgui.EnchantGuiHandler
import com.willfp.ecoskills.libreforge.ConditionHasSkillLevel
import com.willfp.ecoskills.libreforge.EffectAddStat
import com.willfp.ecoskills.libreforge.EffectAddStatTemporarily
import com.willfp.ecoskills.libreforge.EffectGiveSkillXp
import com.willfp.ecoskills.libreforge.EffectMultiplyAllStats
import com.willfp.ecoskills.libreforge.EffectMultiplyStat
import com.willfp.ecoskills.libreforge.EffectMultiplyStatTemporarily
import com.willfp.ecoskills.libreforge.EffectSkillXpMultiplier
import com.willfp.ecoskills.libreforge.FilterSkill
import com.willfp.ecoskills.libreforge.TriggerGainSkillXp
import com.willfp.ecoskills.libreforge.TriggerLevelUpSkill
import com.willfp.ecoskills.placeholders.EcoSkillsTopPlaceholder
import com.willfp.ecoskills.skills.CustomSkills
import com.willfp.ecoskills.skills.SkillDisplayListener
import com.willfp.ecoskills.skills.SkillDisplayListener.Companion.tickBossBars
import com.willfp.ecoskills.skills.SkillLevellingListener
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.CustomStats
import com.willfp.ecoskills.stats.DamageIndicatorListener
import com.willfp.ecoskills.stats.Stats
import com.willfp.ecoskills.stats.customStats
import com.willfp.ecoskills.stats.modifier.StatModifierListener
import com.willfp.libreforge.SimpleProvidedHolder
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory
import com.willfp.libreforge.registerHolderProvider
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.event.Listener

class EcoSkillsPlugin : LibreforgePlugin() {
    val effectsYml = EffectsYml(this)

    override fun loadConfigCategories(): List<ConfigCategory> {
        return listOf(
            CustomStats(), CustomEffects(), CustomSkills()
        )
    }

    override fun handleEnable() {
        com.willfp.libreforge.effects.Effects.register(EffectAddStat)
        com.willfp.libreforge.effects.Effects.register(EffectMultiplyStat)
        com.willfp.libreforge.effects.Effects.register(EffectSkillXpMultiplier)
        com.willfp.libreforge.effects.Effects.register(EffectGiveSkillXp)
        com.willfp.libreforge.effects.Effects.register(EffectMultiplyAllStats)
        com.willfp.libreforge.effects.Effects.register(EffectAddStatTemporarily(this))
        com.willfp.libreforge.effects.Effects.register(EffectMultiplyStatTemporarily(this))
        Conditions.register(ConditionHasSkillLevel)
        Triggers.register(TriggerGainSkillXp)
        Triggers.register(TriggerLevelUpSkill)
        Filters.register(FilterSkill)

        EcoSkillsTopPlaceholder.register(this)
        Skills.update(this)

        registerHolderProvider { it.customEffects.map { h -> SimpleProvidedHolder(h) } }
        registerHolderProvider { it.customStats.map { h -> SimpleProvidedHolder(h) } }
    }

    override fun handleReload() {
        for (effect in Effects.values()) {
            eventManager.unregisterListener(effect)
            eventManager.registerListener(effect)
        }
        for (stat in Stats.values()) {
            eventManager.unregisterListener(stat)
            eventManager.registerListener(stat)
        }
        for (skill in Skills.values()) {
            eventManager.unregisterListener(skill)
            eventManager.registerListener(skill)
        }

        if (configYml.getBool("persistent-action-bar.enabled")) {
            startRunnable()
        }

        tickBossBars(this)
        scheduler.runAsyncTimer(LeaderboardHandler.Runnable(), 50, 2400)
    }

    override fun loadListeners(): List<Listener> {
        return listOf(
            SkillLevellingListener(),
            SkillDisplayListener(this),
            StatModifierListener(),
            DataListener(),
            DamageIndicatorListener(this),
            HealthScaleListener(this),
            ActionBarClearOnGamemode()
        )
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DeprecatedCallableAddReplaceWith")
    override fun loadPacketAdapters(): List<AbstractPacketAdapter> {
        return if (Prerequisite.HAS_1_19.isMet) {
            listOf(
                ActionBarCompatSetActionBar(this)
            )
        } else listOf(
            ActionBarCompatChatMessage(this), ActionBarCompatSetActionBar(this)
        )
    }

    override fun loadPluginCommands(): List<PluginCommand> {
        return listOf(
            CommandEcoSkills(this),
            CommandSkills(this)
        )
    }

    override fun loadIntegrationLoaders(): List<IntegrationLoader> {
        return listOf(IntegrationLoader("EcoEnchants") {
            eventManager.registerListener(EcoEnchantsEnchantingLeveller(this))
        }, IntegrationLoader("EnchantGui") { eventManager.registerListener(EnchantGuiHandler()) })
    }

    companion object {
        @JvmStatic
        fun getInstance(): EcoSkillsPlugin {
            return getPlugin(EcoSkillsPlugin::class.java)
        }
    }
}
