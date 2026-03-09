package com.willfp.ecoskills.effects

import com.willfp.ecoskills.plugin
import com.willfp.libreforge.Holder
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.effects.EffectList

class EffectLevel(
    val effect: Effect,
    val level: Int,
    override val effects: EffectList,
    override val conditions: ConditionList
) : Holder {
    override val id = plugin.createNamespacedKey("${effect.id}_$level")
}