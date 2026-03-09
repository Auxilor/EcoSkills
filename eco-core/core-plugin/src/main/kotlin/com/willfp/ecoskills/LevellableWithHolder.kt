package com.willfp.ecoskills

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Holder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.EffectList
import com.willfp.libreforge.effects.Effects

abstract class LevellableWithHolder(
    id: String,
    config: Config
) : Levellable(id, config) {
    private val levels = mutableMapOf<Int, LevelHolder>()

    private val effects: EffectList
    private val conditions: ConditionList

    init {
        effects = Effects.compile(
            config.getSubsections("effects"),
            ViolationContext(plugin, id)
        )

        conditions = Conditions.compile(
            config.getSubsections("conditions"),
            ViolationContext(plugin, id)
        )
    }

    fun getLevelHolder(level: Int): Holder? = if (level == 0) null else levels.getOrPut(level) {
        LevelHolder(level, conditions, effects)
    }

    private inner class LevelHolder(
        level: Int,
        override val conditions: ConditionList,
        override val effects: EffectList
    ) : Holder {
        override val id = plugin.createNamespacedKey("${this@LevellableWithHolder.id}_$level")
    }
}
