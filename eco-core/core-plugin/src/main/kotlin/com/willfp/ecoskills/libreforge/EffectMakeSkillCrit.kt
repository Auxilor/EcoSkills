package com.willfp.ecoskills.libreforge

import com.willfp.ecoskills.skills.skillCrit
import org.bukkit.event.entity.EntityDamageByEntityEvent
import wiki.ecohub.config.ArgType
import wiki.ecohub.config.Config
import wiki.ecohub.config.arguments
import wiki.ecohub.config.section.getDoubleFromExpression
import wiki.ecohub.core.registry.NoCompileData
import wiki.ecohub.rules.effect.Effect
import wiki.ecohub.rules.effect.RunOrder
import wiki.ecohub.rules.trigger.TriggerData
import wiki.ecohub.rules.trigger.TriggerParameter

object EffectMakeSkillCrit : Effect<NoCompileData>("make_skill_crit") {
    override val description = "Marks the triggering attack as a skill crit, multiplying its damage by the given multiplier."

    override val categories = setOf("combat")

    override val runOrder = RunOrder.EARLY

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the damage multiplier!",
            description = "The multiplier to apply to the attack's damage.",
            type = ArgType.EXPRESSION,
            example = "1 + %level% * 0.1"
        )
    }

    override val parameters = setOf(
        TriggerParameter.EVENT
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val event = data.event as? EntityDamageByEntityEvent ?: return false

        val multiplier = config.getDoubleFromExpression("multiplier", data)
        event.damage *= multiplier
        event.skillCrit *= multiplier

        return true
    }
}
