@file:JvmName("CustomStatUtils")

package com.willfp.ecoskills.stats

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.eco.core.placeholder.PlaceholderInjectable
import com.willfp.eco.core.placeholder.PlayerStaticPlaceholder
import com.willfp.eco.core.placeholder.StaticPlaceholder
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.formatEco
import com.willfp.ecoskills.getStatLevel
import com.willfp.libreforge.Holder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.EffectList
import com.willfp.libreforge.effects.Effects
import org.bukkit.entity.Player
import java.util.*

class CustomStat(
    id: String,
    config: Config
) : Stat(id, config) {
    private val effects: EffectList
    private val conditions: ConditionList

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
            ViolationContext(plugin, "Custom Effect $id")
        )

        conditions = Conditions.compile(
            config.getSubsections("conditions"),
            ViolationContext(plugin, "Custom Effect $id")
        )
    }

    fun getLevel(level: Int): CustomStatLevel = levels.get(level) {
        CustomStatLevel(plugin, this, it, effects, conditions)
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

                    override fun addInjectablePlaceholder(p0: MutableIterable<InjectablePlaceholder>) {
                        // Do nothing.
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

class CustomStatLevel(
    plugin: EcoPlugin,
    parent: CustomStat,
    level: Int,
    override val effects: EffectList,
    override val conditions: ConditionList
) : Holder {
    override val id = plugin.createNamespacedKey("${parent.id}_$level")

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
