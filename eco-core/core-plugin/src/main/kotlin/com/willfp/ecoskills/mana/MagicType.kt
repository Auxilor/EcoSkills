package com.willfp.ecoskills.mana

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.core.placeholder.PlayerlessPlaceholder
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.core.price.Prices
import com.willfp.eco.core.registry.KRegistrable
import com.willfp.eco.util.evaluateExpression
import com.willfp.eco.util.toNiceString
import com.willfp.eco.util.toNumeral
import com.willfp.ecoskills.EcoSkillsPlugin
import org.bukkit.entity.Player

class MagicType(
    override val id: String,
    config: Config,
    plugin: EcoSkillsPlugin
) : KRegistrable {
    private val regenRateExpr = config.getString("regen-rate")

    private val limitExpr = config.getString("limit")

    init {
        PlayerlessPlaceholder(plugin, "${id}_name") {
            plugin.configYml.getFormattedString("mana.name")
        }.register()

        PlayerPlaceholder(plugin, id) {
            it.magic[this].toNiceString()
        }.register()

        PlayerPlaceholder(plugin, "${id}_numeral") {
            it.magic[this].toNumeral()
        }.register()

        PlayerPlaceholder(plugin, "${id}_limit") {
            getLimit(it).toNiceString()
        }
    }

    override fun onRegister() {
        Prices.registerPriceFactory(PriceFactoryMagic(this))
    }

    fun getLimit(player: Player) = evaluateExpression(
        limitExpr, placeholderContext(
            player = player
        )
    ).toInt()

    internal fun tick(player: Player) {
        val toRegen = evaluateExpression(
            regenRateExpr, placeholderContext(
                player = player
            )
        ).toInt()

        player.magic[this] += toRegen
    }
}
