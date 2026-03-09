package com.willfp.ecoskills.magic

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.RegistrableCategory

object MagicTypes : RegistrableCategory<MagicType>("magic_type", "magic_types") {
    override val supportsSharing = false

    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        registry.register(MagicType(id, config))
    }
}
