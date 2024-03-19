package com.willfp.ecoskills.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.api.getMagic
import com.willfp.ecoskills.magic.MagicTypes
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionAboveMagic : Condition<NoCompileData>("above_magic") {
    override val arguments = arguments {
        require("type", "You must specify the magic type!")
        require("amount", "You must specify the amount!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        val type = MagicTypes.getByID(config.getString("type").lowercase()) ?: return false

        return player.getMagic(type) >= config.getIntFromExpression("amount", player)
    }
}
