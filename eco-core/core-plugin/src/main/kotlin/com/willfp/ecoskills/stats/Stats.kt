package com.willfp.ecoskills.stats

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.core.registry.Registry
import com.willfp.eco.util.toNiceString
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.api.averageSkillLevel
import com.willfp.ecoskills.api.totalSkillLevel
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.skills.Skills
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory

object Stats : ConfigCategory("stat", "stats") {
    private val registry = Registry<Stat>()

    override val supportsSharing = false

    override fun beforeReload(plugin: LibreforgePlugin) {
        PlayerPlaceholder(plugin, "total_skill_level") {
            it.totalSkillLevel.toNiceString()
        }.register()

        PlayerPlaceholder(plugin, "average_skill_level") {
            it.averageSkillLevel.toNiceString()
        }.register()
    }

    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        registry.register(Stat(id, config, plugin as EcoSkillsPlugin))
    }

    fun getByID(id: String): Stat? = registry[id]

    fun values(): Set<Stat> = registry.values()
}
