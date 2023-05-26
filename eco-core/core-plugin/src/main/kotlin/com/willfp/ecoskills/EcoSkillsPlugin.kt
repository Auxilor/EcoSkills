package com.willfp.ecoskills

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.packet.PacketListener
import com.willfp.ecoskills.actionbar.ActionBarCompatibilityProxy
import com.willfp.ecoskills.actionbar.ActionBarGamemodeListener
import com.willfp.ecoskills.actionbar.ActionBarHandler
import com.willfp.ecoskills.actionbar.HealthScaleDisabler
import com.willfp.ecoskills.commands.CommandEcoSkills
import com.willfp.ecoskills.commands.CommandSkills
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.libreforge.ConditionAboveMagic
import com.willfp.ecoskills.libreforge.ConditionBelowMagic
import com.willfp.ecoskills.libreforge.ConditionHasSkillLevel
import com.willfp.ecoskills.libreforge.EffectAddStat
import com.willfp.ecoskills.libreforge.EffectAddStatTemporarily
import com.willfp.ecoskills.libreforge.EffectGiveMagic
import com.willfp.ecoskills.libreforge.EffectGiveSkillXp
import com.willfp.ecoskills.libreforge.EffectGiveSkillXpNaturally
import com.willfp.ecoskills.libreforge.EffectMakeSkillCrit
import com.willfp.ecoskills.libreforge.EffectMultiplyAllStats
import com.willfp.ecoskills.libreforge.EffectMultiplyMagic
import com.willfp.ecoskills.libreforge.EffectMultiplyStat
import com.willfp.ecoskills.libreforge.EffectMultiplyStatTemporarily
import com.willfp.ecoskills.libreforge.EffectSkillXpMultiplier
import com.willfp.ecoskills.libreforge.FilterSkill
import com.willfp.ecoskills.libreforge.FilterSkillCrit
import com.willfp.ecoskills.libreforge.TriggerGainSkillXp
import com.willfp.ecoskills.libreforge.TriggerLevelUpSkill
import com.willfp.ecoskills.magic.MagicHandler
import com.willfp.ecoskills.magic.MagicListener
import com.willfp.ecoskills.magic.MagicTypes
import com.willfp.ecoskills.skills.EcoSkillsSkillTopPlaceholder
import com.willfp.ecoskills.skills.EcoSkillsTopPlaceholder
import com.willfp.ecoskills.skills.SkillCritListener
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.skills.display.DamageIndicatorListener
import com.willfp.ecoskills.skills.display.GainXPDisplay
import com.willfp.ecoskills.skills.display.LevelUpDisplay
import com.willfp.ecoskills.skills.display.TemporaryBossBarHandler
import com.willfp.ecoskills.stats.StatModifierListener
import com.willfp.ecoskills.stats.Stats
import com.willfp.libreforge.SimpleProvidedHolder
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory
import com.willfp.libreforge.registerHolderProvider
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.event.Listener

internal lateinit var plugin: EcoSkillsPlugin
    private set

class EcoSkillsPlugin : LibreforgePlugin() {
    init {
        plugin = this
    }

    override fun loadConfigCategories(): List<ConfigCategory> {
        return listOf(
            MagicTypes,
            Stats,
            Effects,
            Skills
        )
    }

    override fun handleEnable() {
        registerHolderProvider { player ->
            (Effects.values() union Stats.values())
                .map { it.getLevelHolder(it.getActualLevel(player)) }
                .map { SimpleProvidedHolder(it) }
        }

        com.willfp.libreforge.effects.Effects.register(EffectAddStat)
        com.willfp.libreforge.effects.Effects.register(EffectMultiplyStat)
        com.willfp.libreforge.effects.Effects.register(EffectSkillXpMultiplier)
        com.willfp.libreforge.effects.Effects.register(EffectGiveSkillXp)
        com.willfp.libreforge.effects.Effects.register(EffectMultiplyAllStats)
        com.willfp.libreforge.effects.Effects.register(EffectMakeSkillCrit)
        com.willfp.libreforge.effects.Effects.register(EffectGiveMagic)
        com.willfp.libreforge.effects.Effects.register(EffectMultiplyMagic)
        com.willfp.libreforge.effects.Effects.register(EffectGiveSkillXpNaturally)
        com.willfp.libreforge.effects.Effects.register(EffectAddStatTemporarily(this))
        com.willfp.libreforge.effects.Effects.register(EffectMultiplyStatTemporarily(this))
        Conditions.register(ConditionHasSkillLevel)
        Conditions.register(ConditionBelowMagic)
        Conditions.register(ConditionAboveMagic)
        Triggers.register(TriggerGainSkillXp)
        Triggers.register(TriggerLevelUpSkill)
        Filters.register(FilterSkill)
        Filters.register(FilterSkillCrit)

        EcoSkillsTopPlaceholder(this).register()
        EcoSkillsSkillTopPlaceholder(this).register()
    }

    override fun handleReload() {
        if (this.configYml.getBool("persistent-action-bar.enabled")) {
            ActionBarHandler(this).startTicking()
        }

        TemporaryBossBarHandler(this).startTicking()
        MagicHandler(this).startTicking()
    }

    override fun loadPluginCommands(): List<PluginCommand> {
        return listOf(
            CommandEcoSkills(this),
            CommandSkills(this)
        )
    }

    override fun loadListeners(): List<Listener> {
        return listOf(
            LevelUpDisplay(this),
            GainXPDisplay(this),
            DamageIndicatorListener(this),
            MagicListener(this),
            HealthScaleDisabler(this),
            ActionBarGamemodeListener,
            SkillCritListener,
            StatModifierListener
        )
    }

    override fun loadPacketListeners(): List<PacketListener> {
        return listOf(
            getProxy(ActionBarCompatibilityProxy::class.java)
        )
    }
}
