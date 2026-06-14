package com.willfp.ecoskills.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.api.gainSkillXP
import com.willfp.ecoskills.skills.Skills
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectGiveSkillXpNaturally : Effect<NoCompileData>("give_skill_xp_naturally") {
    override val description = "Gives the player xp for the given skill as if it was gained naturally, applying xp multipliers."

    override val categories = setOf("economy", "player")

    override val additionalInfo = listOf(
        "The amount gained can be modified or cancelled by other plugins listening to the xp gain event."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "amount",
            "You must specify the amount of xp to give!",
            description = "The base amount of xp to give before multipliers are applied.",
            type = ArgType.EXPRESSION
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
