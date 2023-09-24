package com.willfp.ecoskills.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.api.event.MagicEvent
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterMagicType : Filter<NoCompileData, Collection<String>>("magic_type") {
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
