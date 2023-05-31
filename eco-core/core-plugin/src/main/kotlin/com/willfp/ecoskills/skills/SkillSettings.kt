package com.willfp.ecoskills.skills

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.plugin

class SkillSettings(config: Config) {
    private val settings = config.getSubsectionOrNull("settings")
    private val default: Config
        get() = plugin.configYml.getSubsection("skills")

    fun getConfigFor(path: String): Config {
        return if (settings?.has(path) == true) settings else default
    }
}