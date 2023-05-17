package com.willfp.ecoskills.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.api.gainSkillXP
import com.willfp.ecoskills.skills.Skills
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectGiveSkillXpNaturally : Effect<NoCompileData>("give_skill_xp_naturally") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("amount", "You must specify the amount of xp to give!")
        require("skill", "You must specify the skill to give xp for!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val skill = Skills.getByID(config.getString("skill")) ?: return false

        player.gainSkillXP(skill, config.getDoubleFromExpression("amount", data))

        return true
    }
}
