package com.willfp.ecoskills.libreforge

import com.willfp.ecoskills.api.getMagic
import com.willfp.ecoskills.magic.MagicTypes
import org.bukkit.entity.Player
import wiki.ecohub.config.ArgType
import wiki.ecohub.config.Config
import wiki.ecohub.config.arguments
import wiki.ecohub.core.registry.NoCompileData
import wiki.ecohub.rules.condition.Condition
import wiki.ecohub.rules.holder.Dispatcher
import wiki.ecohub.rules.holder.ProvidedHolder
import wiki.ecohub.rules.holder.get

object ConditionBelowMagic : Condition<NoCompileData>("below_magic") {
    override val description = "Passes when the player's magic of the given type is at or below the maximum amount."

    override val categories = setOf("player")

    override val arguments = arguments {
        require(
            "type",
            "You must specify the magic type!",
            description = "The magic type to check.",
            type = ArgType.STRING
        )
        require(
            "amount",
            "You must specify the amount!",
            description = "The maximum amount of magic allowed.",
            type = ArgType.EXPRESSION,
            example = "50 + %level%"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        val type = MagicTypes.getByID(config.getString("type").lowercase()) ?: return false

        return player.getMagic(type) <= config.getIntFromExpression("amount", player)
    }
}
