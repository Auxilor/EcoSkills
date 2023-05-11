package com.willfp.ecoskills

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.packet.PacketListener
import com.willfp.ecoskills.actionbar.ActionBarCompatibilityProxy
import com.willfp.ecoskills.actionbar.ActionBarGamemodeListener
import com.willfp.ecoskills.actionbar.ActionBarHandler
import com.willfp.ecoskills.commands.CommandEcoSkills
import com.willfp.ecoskills.commands.CommandSkills
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.skills.EcoSkillsTopPlaceholder
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.skills.SkillLevelUpListener
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stats
import com.willfp.libreforge.SimpleProvidedHolder
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory
import com.willfp.libreforge.registerHolderProvider
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
        EcoSkillsTopPlaceholder(this).register()
    }

    override fun handleReload() {
        ActionBarHandler(this).beginTickingActionBar()
    }

    override fun loadPluginCommands(): List<PluginCommand> {
        return listOf(
            CommandEcoSkills(this),
            CommandSkills(this)
        )
    }

    override fun loadListeners(): List<Listener> {
        return listOf(
            SkillLevelUpListener(this),
            ActionBarGamemodeListener
        )
    }

    override fun loadPacketListeners(): List<PacketListener> {
        return listOf(
            getProxy(ActionBarCompatibilityProxy::class.java)
        )
    }
}
