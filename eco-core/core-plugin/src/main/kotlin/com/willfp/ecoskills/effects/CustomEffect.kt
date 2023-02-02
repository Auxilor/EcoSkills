package com.willfp.ecoskills.effects

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.eco.core.placeholder.PlaceholderInjectable
import com.willfp.eco.core.placeholder.PlayerStaticPlaceholder
import com.willfp.eco.core.placeholder.StaticPlaceholder
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.getEffectLevel
import com.willfp.libreforge.Holder
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.conditions.ConfiguredCondition
import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.effects.Effects
import org.bukkit.entity.Player
import java.util.*

class CustomEffect(
    id: String,
    config: Config
) : Effect(id, config) {
    private val effects: Set<ConfiguredEffect>
    private val conditions: Set<ConfiguredCondition>

    private val levels = Caffeine.newBuilder()
        .build<Int, CustomEffectLevel>()
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

        effects = Effects.compile(
            config.getSubsections("effects"),
            "Custom Effect $id"
        )

        conditions = Conditions.compile(
            config.getSubsections("conditions"),
            "Custom Effect $id"
        )
    }

    fun getLevel(level: Int): CustomEffectLevel = levels.get(level) {
        CustomEffectLevel(this, it, effects, conditions)
    }

    override fun formatDescription(string: String, level: Int): String {
        return levelDescriptions.get(level) {
        val uncompiledPlaceholders = config.getSubsection("placeholders").getKeys(false).associateWith {
            config.getString("placeholders.$it")
        }.toMutableMap()

        // Add %placeholder% placeholder in
        uncompiledPlaceholders["placeholder"] = config.getString("placeholder")

        // Evaluate each placeholder
        val placeholders = uncompiledPlaceholders.map { (id, expr) ->
            DescriptionPlaceholder(
                    id,
                    NumberUtils.evaluateExpression(
                            expr,
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
                    )
            )
        }

        // Apply placeholders to description
        val rawDescription = config.getString("description")
        var description = rawDescription
        for (placeholder in placeholders) {
            description = description.replace("%${placeholder.id}%", NumberUtils.format(placeholder.value))
        }

        description
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
