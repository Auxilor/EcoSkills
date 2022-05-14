@file:JvmName("CustomEffectUtils")

package com.willfp.ecoskills.stats

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.BaseConfig
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.PlayerStaticPlaceholder
import com.willfp.ecoskills.getStatLevel
import com.willfp.libreforge.Holder
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.conditions.ConfiguredCondition
import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.effects.Effects
import org.bukkit.entity.Player
import java.util.Objects

class CustomStat(
    config: Config
) : Stat(config.getString("id"), config) {
    private val effects: Set<ConfiguredEffect>
    private val conditions: Set<ConfiguredCondition>

    private val levels = mutableMapOf<Int, CustomStatLevel>()
    private val levelDescriptions = Caffeine.newBuilder()
        .build<Int, String>()

    init {
        config.injectPlaceholders(
            PlayerStaticPlaceholder(
                "level"
            ) { p ->
                p.getStatLevel(this).toString()
            }
        )

        effects = config.getSubsections("effects").mapNotNull {
            Effects.compile(it, "Custom Effect $id")
        }.toSet()

        conditions = config.getSubsections("conditions").mapNotNull {
            Conditions.compile(it, "Custom Effect $id")
        }.toSet()

        repeat(config.getInt("max-level")) {
            levels[it] = CustomStatLevel(this, it, effects, conditions)
        }
    }

    fun getLevel(level: Int) = levels[level]!!
}

internal val Player.customStats: Collection<Holder>
    get() {
        val stats = mutableListOf<Holder>()

        for (stat in CustomStats.values()) {
            val level = this.getStatLevel(stat)
            if (level > 0) {
                stats.add(stat.getLevel(level))
            }
        }

        return stats
    }

class CustomStatsYml(
    plugin: EcoPlugin
) : BaseConfig(
    "customstats",
    plugin,
    true,
    ConfigType.YAML
)

class CustomStatLevel(
    parent: CustomStat,
    level: Int,
    override val effects: Set<ConfiguredEffect>,
    override val conditions: Set<ConfiguredCondition>
) : Holder {
    override val id = "${parent.id}_$level"

    override fun equals(other: Any?): Boolean {
        if (other !is CustomStatLevel) {
            return false
        }

        return this.id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(this.id)
    }
}
