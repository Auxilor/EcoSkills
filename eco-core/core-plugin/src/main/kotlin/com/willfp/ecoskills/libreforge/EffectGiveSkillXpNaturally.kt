package com.willfp.ecoskills.libreforge

import com.willfp.ecoskills.api.gainSkillXP
import com.willfp.ecoskills.skills.Skills
import wiki.ecohub.config.ArgType
import wiki.ecohub.config.Config
import wiki.ecohub.config.arguments
import wiki.ecohub.config.section.getDoubleFromExpression
import wiki.ecohub.core.registry.NoCompileData
import wiki.ecohub.rules.effect.Effect
import wiki.ecohub.rules.trigger.TriggerData
import wiki.ecohub.rules.trigger.TriggerParameter

object EffectGiveSkillXpNaturally : Effect<NoCompileData>("give_skill_xp_naturally") {
    override val description = "Gives the player xp for the given skill as if it was gained naturally, applying xp multipliers."

    override val categories = setOf("economy", "player")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "amount",
            "You must specify the amount of xp to give!",
            description = "The base amount of xp to give before multipliers are applied.",
            type = ArgType.EXPRESSION,
            example = "10 + %level%"
        )
        require(
            "skill",
            "You must specify the skill to give xp for!",
            description = "The skill to give xp for.",
            type = ArgType.STRING
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val skill = Skills.getByID(config.getString("skill")) ?: return false

        player.gainSkillXP(skill, config.getDoubleFromExpression("amount", data))

        return true
    }
}
