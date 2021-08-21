package com.willfp.ecoskills.config

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.yaml.YamlExtendableConfig

class SkillConfig(
    name: String,
    source: Class<*>,
    plugin: EcoPlugin
) : YamlExtendableConfig(
    name,
    true,
    plugin,
    source,
    "skills/",
    "rewards-messages",
    "rewards-gui-lore"
) {
}