package com.willfp.ecoskills.skills

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registry
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.gui.menus.SkillsGUI
import com.willfp.ecoskills.gui.menus.StatsGUI
import com.willfp.ecoskills.util.InvalidConfigurationException
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory

object Skills : ConfigCategory("skill", "skills") {
    private val registry = Registry<Skill>()

    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        try {
            registry.register(Skill(id, config, plugin as EcoSkillsPlugin))
        } catch (e: InvalidConfigurationException) {
            plugin.logger.warning("Failed to load skill $id: ${e.message}")
        }
    }

    override fun afterReload(plugin: LibreforgePlugin) {
        SkillsGUI.update(plugin)
    }

    fun getByID(id: String): Skill? = registry[id]

    fun values(): Set<Skill> = registry.values()
}
