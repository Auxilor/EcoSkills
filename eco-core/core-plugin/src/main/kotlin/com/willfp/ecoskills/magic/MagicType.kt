package com.willfp.ecoskills.magic

import com.willfp.ecoskills.api.event.PlayerRegenMagicEvent
import com.willfp.ecoskills.libreforge.EffectArgumentMagicCost
import com.willfp.ecoskills.plugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import wiki.ecohub.config.Config
import wiki.ecohub.core.price.Prices
import wiki.ecohub.core.registry.KRegistrable
import wiki.ecohub.core.stdlib.toNiceString
import wiki.ecohub.math.evaluateExpression
import wiki.ecohub.math.toNumeral
import wiki.ecohub.rules.argument.EffectArguments
import wiki.ecohub.ui.placeholder.placeholderContext
import wiki.ecohub.ui.placeholder.type.PlayerPlaceholder
import wiki.ecohub.ui.placeholder.type.PlayerlessPlaceholder
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
        PlayerlessPlaceholder(plugin, "${id}_name") {
            config.getFormattedString("name")
        }.register()

        PlayerPlaceholder(plugin, id) {
            it.magic[this].toNiceString()
        }.register()

        PlayerPlaceholder(plugin, "${id}_numeral") {
            it.magic[this].toNumeral()
        }.register()

        PlayerPlaceholder(plugin, "${id}_limit") {
            getLimit(it).toNiceString()
        }.register()
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

        if (baseToRegen >= 0 && player.magic[this] >= getLimit(player)) {
            return
        }

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
