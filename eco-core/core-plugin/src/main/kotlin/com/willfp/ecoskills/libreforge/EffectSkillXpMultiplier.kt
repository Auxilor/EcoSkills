package com.willfp.ecoskills.libreforge

import com.willfp.ecoskills.api.PlayerSkillExpGainEvent
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.skills.Skills
import com.willfp.libreforge.effects.templates.MultiMultiplierEffect
import org.bukkit.event.EventHandler

object EffectSkillXpMultiplier : MultiMultiplierEffect<Skill>("skill_xp_multiplier") {
    override val key = "skills"

    override fun getElement(key: String): Skill? {
        return Skills.getByID(key)
    }

    override fun getAllElements(): Collection<Skill> {
        return Skills.values()
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerSkillExpGainEvent) {
        val player = event.player

        event.amount *= getMultiplier(player, event.skill)
    }
}
