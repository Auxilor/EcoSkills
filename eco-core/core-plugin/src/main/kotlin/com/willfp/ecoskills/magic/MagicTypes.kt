package com.willfp.ecoskills.magic

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.CategoryWithRegistry
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.libreforge.loader.LibreforgePlugin

object MagicTypes : CategoryWithRegistry<MagicType>("magic_type", "magic_types") {
    override val supportsSharing = false

    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        registry.register(MagicType(id, config, plugin as EcoSkillsPlugin))
    }
}
