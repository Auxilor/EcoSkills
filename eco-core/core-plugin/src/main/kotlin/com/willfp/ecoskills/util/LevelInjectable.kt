package com.willfp.ecoskills.util

import wiki.ecohub.ui.placeholder.InjectablePlaceholder
import wiki.ecohub.ui.placeholder.PlaceholderInjectable
import wiki.ecohub.ui.placeholder.type.StaticPlaceholder

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
