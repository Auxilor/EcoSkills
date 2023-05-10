package com.willfp.ecoskills

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecoskills.commands.CommandEcoSkills
import com.willfp.ecoskills.commands.CommandSkills
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.skills.EcoSkillsTopPlaceholder
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stats
import com.willfp.libreforge.SimpleProvidedHolder
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory
import com.willfp.libreforge.registerHolderProvider

internal lateinit var plugin: EcoSkillsPlugin
    private set

class EcoSkillsPlugin : LibreforgePlugin() {
    init {
        plugin = this

        registerHolderProvider { player ->
            (Effects.values().map { it.getLevelHolder(it.getActualLevel(player)) } +
                    Stats.values().map { it.getLevelHolder(it.getActualLevel(player)) })
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

    override fun loadPluginCommands(): List<PluginCommand> {
        return listOf(
            CommandEcoSkills(this),
            CommandSkills(this)
        )
    }
}
