package com.willfp.ecoskills.effects

import com.willfp.ecoskills.EcoSkillsPlugin
import wiki.ecohub.config.Config
import wiki.ecohub.config.category.RegistrableCategory
import wiki.ecohub.core.plugin.EcoPlugin

object Effects : RegistrableCategory<Effect>("effect", "effects") {
    override val supportsSharing = false

    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        registry.register(Effect(id, config, plugin as EcoSkillsPlugin))
    }
}
