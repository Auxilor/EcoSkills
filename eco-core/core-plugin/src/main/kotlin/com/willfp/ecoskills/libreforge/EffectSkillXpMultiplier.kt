package com.willfp.ecoskills.libreforge

import com.willfp.ecoskills.api.event.PlayerSkillXPGainEvent
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.skills.Skills
import org.bukkit.event.EventHandler
import wiki.ecohub.config.ArgType
import wiki.ecohub.config.arguments
import wiki.ecohub.game.entity.toDispatcher
import wiki.ecohub.rules.effect.MultiMultiplierEffect

object EffectSkillXpMultiplier : MultiMultiplierEffect<Skill>("skill_xp_multiplier") {
    override val description = "Multiplies XP gained for one or all EcoSkills skills while the holder is active."

    override val categories = setOf("player")

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the multiplier!",
            description = "The XP multiplier. Supports expressions.",
            type = ArgType.EXPRESSION
        )
        optional(
            "skills",
            description = "List of skill names to apply the multiplier to. If omitted, applies to all skills.",
            type = ArgType.STRING_LIST
        )
    }

    override val key = "skills"

    override fun getElement(key: String): Skill? {
        return Skills.getByID(key)
    }

    override fun getAllElements(): Collection<Skill> {
        return Skills.values()
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerSkillXPGainEvent) {
        val player = event.player

        event.gainedXP *= getMultiplier(player.toDispatcher(), event.skill)
    }
}
