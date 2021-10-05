package com.willfp.ecoskills.attackspeed

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.yaml.YamlExtendableConfig

class AttackSpeedConfig(plugin: EcoPlugin) : YamlExtendableConfig(
    "attackspeed",
    true,
    plugin,
    AttackSpeedConfig::class.java,
    "stats/"
) {
}