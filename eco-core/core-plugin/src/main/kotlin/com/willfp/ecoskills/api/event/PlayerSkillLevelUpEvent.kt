package com.willfp.ecoskills.api.event

import com.willfp.ecoskills.skills.Skill
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

class PlayerSkillLevelUpEvent(
    who: Player,
    skill: Skill,
    val level: Int
) : PlayerSkillEvent(who, skill) {
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
