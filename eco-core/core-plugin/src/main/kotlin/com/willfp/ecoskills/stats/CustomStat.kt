@file:JvmName("CustomStatUtils")

package com.willfp.ecoskills.stats

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.BaseConfig
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.eco.core.placeholder.PlaceholderInjectable
import com.willfp.eco.core.placeholder.PlayerStaticPlaceholder
import com.willfp.eco.core.placeholder.StaticPlaceholder
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.formatEco
import com.willfp.ecoskills.getStatLevel
import com.willfp.libreforge.Holder
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.conditions.ConfiguredCondition
import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.effects.Effects
import org.bukkit.entity.Player
import java.util.*

class CustomStat(
    config: Config
) : Stat(config.getString("id"), config) {
    private val effects: Set<ConfiguredEffect>
    private val conditions: Set<ConfiguredCondition>

    private val levels = Caffeine.newBuilder()
        .build<Int, CustomStatLevel>()
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

        effects = Effects.compile(
            config.getSubsections("effects"),
            "Custom Effect $id"
        )

        conditions = Conditions.compile(
            config.getSubsections("conditions"),
            "Custom Effect $id"
        )
    }

    fun getLevel(level: Int): CustomStatLevel = levels.get(level) {
        CustomStatLevel(this, it, effects, conditions)
    }

    override fun formatDescription(string: String, level: Int): String {
        return levelDescriptions.get(level) {
            val placeholderValue = NumberUtils.evaluateExpression(
                config.getString("placeholder"),
                null,
                object : PlaceholderInjectable {
                    override fun getPlaceholderInjections(): List<InjectablePlaceholder> {
                        return listOf(
                            StaticPlaceholder(
                                "level",
                            ) { level.toString() }
                        )
                    }

                    override fun clearInjectedPlaceholders() {
                        // Do nothing
                    }
                }
            ).toString()

            config.getString("description")
                .replace("%placeholder%", placeholderValue)
                .formatEco()
        }
    }
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
