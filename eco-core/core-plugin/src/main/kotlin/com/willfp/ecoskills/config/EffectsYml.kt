package com.willfp.ecoskills.config

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.BaseConfig
import com.willfp.eco.core.config.ConfigType

class EffectsYml(
    plugin: EcoPlugin
): BaseConfig(
    "effects",
    plugin,
    true,
    ConfigType.YAML
)