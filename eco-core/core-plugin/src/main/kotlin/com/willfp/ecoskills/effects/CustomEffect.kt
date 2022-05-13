@file:JvmName("CustomEffectUtils")

package com.willfp.ecoskills.effects

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
import com.willfp.ecoskills.getEffectLevel
import com.willfp.libreforge.Holder
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.conditions.ConfiguredCondition
import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.effects.Effects
import org.bukkit.entity.Player
import java.util.*

class CustomEffect(
    config: Config
) : Effect(config.getString("id"), config) {
    private val effects: Set<ConfiguredEffect>
    private val conditions: Set<ConfiguredCondition>

    private val levels = mutableMapOf<Int, CustomEffectLevel>()
    private val levelDescriptions = Caffeine.newBuilder()
        .build<Int, String>()

    init {
        config.injectPlaceholders(
            PlayerStaticPlaceholder(
                "level"
            ) { p ->
                p.getEffectLevel(this).toString()
            }
        )

        effects = config.getSubsections("effects").mapNotNull {
            Effects.compile(it, "Custom Effect $id")
        }.toSet()

        conditions = config.getSubsections("conditions").mapNotNull {
            Conditions.compile(it, "Custom Effect $id")
        }.toSet()

        repeat(config.getInt("max-level")) {
            levels[it] = CustomEffectLevel(this, it, effects, conditions)
        }
    }

    fun getLevel(level: Int) = levels[level]!!

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

internal val Player.customEffects: Collection<Holder>
    get() {
        val effects = mutableListOf<Holder>()

        for (effect in CustomEffects.values()) {
            val level = this.getEffectLevel(effect)
            if (level > 0) {
                effects.add(effect.getLevel(level))
            }
        }

        return effects
    }

class CustomEffectsYml(
    plugin: EcoPlugin
) : BaseConfig(
    "customeffects",
    plugin,
    true,
    ConfigType.YAML
)

class CustomEffectLevel(
    parent: CustomEffect,
    level: Int,
    override val effects: Set<ConfiguredEffect>,
    override val conditions: Set<ConfiguredCondition>
) : Holder {
    override val id = "${parent.id}_$level"

    override fun equals(other: Any?): Boolean {
        if (other !is CustomEffectLevel) {
            return false
        }

        return this.id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(this.id)
    }
}
