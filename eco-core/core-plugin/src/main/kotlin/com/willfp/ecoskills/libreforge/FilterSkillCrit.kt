package com.willfp.ecoskills.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.skills.isSkillCrit
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.event.entity.EntityDamageByEntityEvent

object FilterSkillCrit : Filter<NoCompileData, Boolean>("skill_crit") {
    override val description = "Matches when whether the triggering attack was a skill crit equals the given value."

    override val categories = setOf("combat")

    override val valueType = ArgType.BOOLEAN

    override val additionalInfo = listOf(
        "Passes automatically when the trigger event is not a damage event."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val event = data.event as? EntityDamageByEntityEvent ?: return true

        return event.isSkillCrit == value
    }
}
