package com.willfp.ecoskills.magic

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.core.price.Prices
import com.willfp.eco.core.registry.KRegistrable
import com.willfp.eco.util.evaluateExpression
import com.willfp.ecoskills.Placeholders
import com.willfp.ecoskills.api.event.PlayerRegenMagicEvent
import com.willfp.ecoskills.libreforge.EffectArgumentMagicCost
import com.willfp.libreforge.effects.arguments.EffectArguments
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.math.ceil

class MagicType(
    override val id: String,
    config: Config
) : KRegistrable {
    private val regenRateExpr = config.getString("regen-rate")

    private val limitExpr = config.getString("limit")

    val name = config.getFormattedString("name")

    val joinOnFull = config.getBool("join-on-full")

    val priceFactory = PriceFactoryMagic(this)

    init {
        Placeholders.applyExternalMagicPlaceholders(this)
    }

    override fun onRegister() {
        Prices.registerPriceFactory(priceFactory)
        EffectArguments.register(EffectArgumentMagicCost(this))
    }

    fun getLimit(player: Player) = evaluateExpression(
        limitExpr, placeholderContext(
            player = player
        )
    ).toInt()

    internal fun tick(player: Player) {
        val baseToRegen = ceil(
            evaluateExpression(
                regenRateExpr, placeholderContext(
                    player = player
                )
            )
        ).toInt()

        val event = PlayerRegenMagicEvent(player, this, baseToRegen)
        Bukkit.getPluginManager().callEvent(event)

        player.magic[this] += event.amount
    }

    override fun equals(other: Any?): Boolean {
        return other is MagicType && other.id == this.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
