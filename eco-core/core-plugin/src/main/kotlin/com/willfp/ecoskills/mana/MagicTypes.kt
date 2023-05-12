package com.willfp.ecoskills.mana

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registry
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory

object MagicTypes : ConfigCategory("magic_type", "magic_types") {
    private val registry = Registry<MagicType>()

    override val supportsSharing = false

    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        registry.register(MagicType(id, config, plugin as EcoSkillsPlugin))
    }

    fun getByID(id: String): MagicType? = registry[id]

    fun values(): Set<MagicType> = registry.values()
}
