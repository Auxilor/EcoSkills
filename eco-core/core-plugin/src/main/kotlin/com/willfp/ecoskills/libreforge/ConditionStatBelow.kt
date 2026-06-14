package com.willfp.ecoskills.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.api.getStatLevel
import com.willfp.ecoskills.stats.Stats
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

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
            type = ArgType.EXPRESSION
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
