package com.willfp.ecoskills.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.api.giveSkillXP
import com.willfp.ecoskills.skills.Skills
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectGiveSkillXp : Effect<NoCompileData>("give_skill_xp") {
    override val description = "Gives the player a flat amount of xp for the given skill."

    override val categories = setOf("economy", "player")

    override val additionalInfo = listOf(
        "Unlike give_skill_xp_naturally, this bypasses xp multipliers and cannot be cancelled."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "amount",
            "You must specify the amount of xp to give!",
            description = "The amount of xp to give.",
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

        player.giveSkillXP(skill, config.getDoubleFromExpression("amount", data))

        return true
    }
}
