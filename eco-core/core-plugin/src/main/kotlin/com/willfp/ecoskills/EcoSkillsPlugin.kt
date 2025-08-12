package com.willfp.ecoskills

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.packet.PacketListener
import com.willfp.ecoskills.actionbar.ActionBarCompatibilityProxy
import com.willfp.ecoskills.actionbar.ActionBarGamemodeListener
import com.willfp.ecoskills.actionbar.ActionBarHandler
import com.willfp.ecoskills.actionbar.HealthScaleDisabler
import com.willfp.ecoskills.commands.CommandEcoSkills
import com.willfp.ecoskills.commands.CommandSkills
import com.willfp.ecoskills.commands.CommandStats
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.libreforge.*
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
import com.willfp.ecoskills.skills.isInDisabledWorld
import com.willfp.ecoskills.stats.StatModifierListener
import com.willfp.ecoskills.stats.Stats
import com.willfp.libreforge.SimpleProvidedHolder
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory
import com.willfp.libreforge.registerSpecificHolderProvider
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.entity.Player
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
        registerSpecificHolderProvider<Player> { player ->
            if (player.isInDisabledWorld) emptyList() else
                (Effects.values() union Stats.values())
                    .mapNotNull { it.getLevelHolder(it.getActualLevel(player)) }
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
        com.willfp.libreforge.effects.Effects.register(EffectMagicRegenMultiplier)
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

        EcoSkillsTopPlaceholder(this).register()
        EcoSkillsSkillTopPlaceholder(this).register()
        Skills.registerPlaceholders(this)
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
            CommandSkills(this),
            CommandStats(this)
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
