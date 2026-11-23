package com.willfp.ecoskills.libreforge

import com.willfp.ecoskills.api.event.MagicEvent
import wiki.ecohub.config.ArgType
import wiki.ecohub.config.Config
import wiki.ecohub.core.registry.NoCompileData
import wiki.ecohub.rules.filter.Filter
import wiki.ecohub.rules.trigger.TriggerData

object FilterMagicType : Filter<NoCompileData, Collection<String>>("magic_type") {
    override val description = "Matches when the magic type involved in the event is one of the given types."

    override val categories = setOf("player")

    override val valueType = ArgType.STRING_LIST

    override val additionalInfo = listOf(
        "Passes automatically when the trigger event is not a magic-related event."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? MagicEvent ?: return true

        return value.any { skillName ->
            skillName.equals(event.magicType.id, ignoreCase = true)
        }
    }
}
