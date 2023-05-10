package com.willfp.ecoskills.util

import com.willfp.eco.core.config.interfaces.Config

data class DescriptionPlaceholder(
    val id: String,
    val expr: String
)

fun loadDescriptionPlaceholders(config: Config): List<DescriptionPlaceholder> {
    val placeholders = mutableListOf(
        DescriptionPlaceholder(
            "placeholder",
            config.getString("placeholder"),
        )
    )

    for (key in config.getSubsection("placeholders").getKeys(false)) {
        placeholders += DescriptionPlaceholder(
            key,
            config.getString("placeholders.$key"),
        )
    }

    return placeholders
}
