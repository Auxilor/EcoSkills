package com.willfp.ecoskills

import wiki.ecohub.config.Config
import wiki.ecohub.config.ViolationContext
import wiki.ecohub.rules.condition.ConditionList
import wiki.ecohub.rules.condition.Conditions
import wiki.ecohub.rules.effect.EffectList
import wiki.ecohub.rules.effect.Effects
import wiki.ecohub.rules.holder.Holder
import java.util.concurrent.ConcurrentHashMap

abstract class LevellableWithHolder(
    id: String,
    config: Config
) : Levellable(id, config) {
    private val levels = ConcurrentHashMap<Int, LevelHolder>()

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

    fun getLevelHolder(level: Int): Holder? = if (level == 0) null else levels.computeIfAbsent(level) {
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
