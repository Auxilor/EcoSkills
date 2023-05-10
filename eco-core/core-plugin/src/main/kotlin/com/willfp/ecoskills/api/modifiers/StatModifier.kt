package com.willfp.ecoskills.api.modifiers

import com.willfp.ecoskills.stats.Stat
import java.util.UUID

data class StatModifier(
    val uuid: UUID,
    val stat: Stat,
    val modifier: Double,
    val operation: ModifierOperation
)
