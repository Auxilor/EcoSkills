package com.willfp.ecoskills.magic

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
import com.willfp.ecoskills.api.event.PlayerRegenMagicEvent
import com.willfp.ecoskills.libreforge.EffectArgumentMagicCost
import com.willfp.libreforge.effects.arguments.EffectArguments
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.math.ceil

class MagicType(
    override val id: String,
    config: Config,
    plugin: EcoSkillsPlugin
) : KRegistrable {
    private val regenRateExpr = config.getString("regen-rate")

    private val limitExpr = config.getString("limit")

    val joinOnFull = config.getBool("join-on-full")

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
        Prices.registerPriceFactory(PriceFactoryMagic(this))
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
