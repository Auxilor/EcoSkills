package com.willfp.ecoskills.libreforge

import com.willfp.ecoskills.api.getStatLevel
import com.willfp.ecoskills.stats.Stats
import org.bukkit.entity.Player
import wiki.ecohub.config.ArgType
import wiki.ecohub.config.Config
import wiki.ecohub.config.arguments
import wiki.ecohub.core.registry.NoCompileData
import wiki.ecohub.rules.condition.Condition
import wiki.ecohub.rules.holder.Dispatcher
import wiki.ecohub.rules.holder.ProvidedHolder
import wiki.ecohub.rules.holder.get

object ConditionStatBelow : Condition<NoCompileData>("stat_below") {
    override val description = "Passes when the player's level in the given stat is below the maximum."

    override val categories = setOf("player")

    override val arguments = arguments {
        require(
            "stat",
            "You must specify the stat!",
            description = "The stat to check the level of.",
            type = ArgType.STRING
        )
        require(
            "level",
            "You must specify the level!",
            description = "The maximum stat level allowed.",
            type = ArgType.EXPRESSION,
            example = "5 + %level%"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        val type = Stats.getByID(config.getString("stat").lowercase()) ?: return false

        return player.getStatLevel(type) < config.getIntFromExpression("level", player)
    }
}
