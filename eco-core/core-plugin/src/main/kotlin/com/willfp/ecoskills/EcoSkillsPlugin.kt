package com.willfp.ecoskills

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.packet.PacketListener
import com.willfp.ecoskills.actionbar.ActionBarCompatibilityProxy
import com.willfp.ecoskills.actionbar.ActionBarGamemodeListener
import com.willfp.ecoskills.actionbar.ActionBarHandler
import com.willfp.ecoskills.commands.CommandEcoSkills
import com.willfp.ecoskills.commands.CommandSkills
import com.willfp.ecoskills.effects.Effects
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
import com.willfp.ecoskills.skills.EcoSkillsTopPlaceholder
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.skills.display.DamageIndicatorListener
import com.willfp.ecoskills.skills.display.GainXPDisplay
import com.willfp.ecoskills.skills.display.LevelUpDisplay
import com.willfp.ecoskills.skills.display.TemporaryBossBarHandler
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

        registerHolderProvider { player ->
            (Effects.values() union Stats.values())
                .map { it.getLevelHolder(it.getActualLevel(player)) }
                .map { SimpleProvidedHolder(it) }
        }
    }

    override fun loadConfigCategories(): List<ConfigCategory> {
        return listOf(
            Stats,
            Effects,
            Skills
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

        EcoSkillsTopPlaceholder(this).register()
    }

    override fun handleReload() {
        ActionBarHandler(this).startTicking()
        TemporaryBossBarHandler(this).startTicking()
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
            ActionBarGamemodeListener
        )
    }

    override fun loadPacketListeners(): List<PacketListener> {
        return listOf(
            getProxy(ActionBarCompatibilityProxy::class.java)
        )
    }
}
