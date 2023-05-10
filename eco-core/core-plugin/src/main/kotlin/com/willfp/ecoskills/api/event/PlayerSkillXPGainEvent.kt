package com.willfp.ecoskills.api.event

import com.willfp.ecoskills.skills.Skill
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList

class PlayerSkillXPGainEvent(
    who: Player,
    skill: Skill,
    var xp: Double
) : PlayerSkillEvent(who, skill), Cancellable {
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
