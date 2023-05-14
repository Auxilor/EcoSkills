package com.willfp.ecoskills.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.CategoryWithRegistry
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.libreforge.loader.LibreforgePlugin

object Effects : CategoryWithRegistry<Effect>("effect", "effects") {
    override val supportsSharing = false

    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        registry.register(Effect(id, config, plugin as EcoSkillsPlugin))
    }
}
