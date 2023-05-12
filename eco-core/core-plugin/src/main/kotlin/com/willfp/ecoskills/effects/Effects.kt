package com.willfp.ecoskills.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registry
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory

object Effects : ConfigCategory("effect", "effects") {
    private val registry = Registry<Effect>()

    override val supportsSharing = false

    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        registry.register(Effect(id, config, plugin as EcoSkillsPlugin))
    }

    fun getByID(id: String): Effect? = registry[id]

    fun values(): Set<Effect> = registry.values()
}
