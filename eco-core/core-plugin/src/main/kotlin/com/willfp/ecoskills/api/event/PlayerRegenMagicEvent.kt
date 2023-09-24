package com.willfp.ecoskills.api.event

import com.willfp.ecoskills.magic.MagicType
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class PlayerRegenMagicEvent(
    who: Player,
    override val magicType: MagicType,
    var amount: Int
) : PlayerEvent(who), MagicEvent, Cancellable {
    private var _cancelled = false

    override fun isCancelled() = _cancelled

    override fun setCancelled(cancel: Boolean) {
        _cancelled = cancel
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlerList
        }
    }
}
