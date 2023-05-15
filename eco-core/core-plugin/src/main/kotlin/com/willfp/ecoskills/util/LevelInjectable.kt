package com.willfp.ecoskills.util

import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.eco.core.placeholder.PlaceholderInjectable
import com.willfp.eco.core.placeholder.StaticPlaceholder

class LevelInjectable(
    level: Int
) : PlaceholderInjectable {
    private val placeholders = listOf(
        StaticPlaceholder(
            "level"
        ) { level.toString() }
    )

    override fun getPlaceholderInjections(): List<InjectablePlaceholder> {
        return placeholders
    }

    override fun addInjectablePlaceholder(p0: Iterable<InjectablePlaceholder>) {
        return
    }

    override fun clearInjectedPlaceholders() {
        return
    }
}
