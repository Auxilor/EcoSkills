package com.willfp.ecoskills.config

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.ExtendableConfig

class SkillConfig(
    name: String,
    source: Class<*>,
    plugin: EcoPlugin
) : ExtendableConfig(
    name,
    true,
    plugin,
    source,
    "skills/",
    ConfigType.YAML,
    "rewards.progression-lore",
    "rewards.chat-messages",
)