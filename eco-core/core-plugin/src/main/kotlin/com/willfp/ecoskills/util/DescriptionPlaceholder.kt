package com.willfp.ecoskills.util

import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.toNiceString

data class DescriptionPlaceholder(
    val id: String,
    val expr: String
) {
    fun getValue(level: Int): String {
        return NumberUtils.evaluateExpression(
            expr,
            placeholderContext(
                injectable = LevelInjectable(level)
            )
        ).toNiceString()
    }
}
