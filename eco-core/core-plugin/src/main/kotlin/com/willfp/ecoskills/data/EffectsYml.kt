package com.willfp.ecoskills.data

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.yaml.YamlBaseConfig

class EffectsYml(
    plugin: EcoPlugin
): YamlBaseConfig(
    "effects",
    true,
    plugin
) {
}