package com.willfp.ecoskills.effects

import com.willfp.ecoskills.plugin
import wiki.ecohub.rules.condition.ConditionList
import wiki.ecohub.rules.effect.EffectList
import wiki.ecohub.rules.holder.Holder

class EffectLevel(
    val effect: Effect,
    val level: Int,
    override val effects: EffectList,
    override val conditions: ConditionList
) : Holder {
    override val id = plugin.createNamespacedKey("${effect.id}_$level")
}