package com.willfp.ecoskills.stats

import com.willfp.eco.core.EcoPlugin
import com.willfp.ecoskills.skills.Skill
import com.willfp.libreforge.Holder
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.effects.EffectList

class StatLevel(
    plugin: EcoPlugin,
    val stat: Stat,
    val level: Int,
    override val effects: EffectList,
    override val conditions: ConditionList
) : Holder {
    override val id = plugin.createNamespacedKey("${stat.id}_$level")
}