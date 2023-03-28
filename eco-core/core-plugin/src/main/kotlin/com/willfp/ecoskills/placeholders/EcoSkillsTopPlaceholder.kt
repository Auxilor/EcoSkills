package com.willfp.ecoskills.placeholders

import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.DynamicPlaceholder
import com.willfp.eco.util.savedDisplayName
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.skills.Skills
import java.util.regex.Pattern

object EcoSkillsTopPlaceholder {
    @JvmStatic
    fun register(plugin: EcoSkillsPlugin) {
        PlaceholderManager.registerPlaceholder(
            DynamicPlaceholder(
                plugin,
                Pattern.compile("(top_)[a-z]+_[0-9]+_[a-z]+")
            ) { getValue(it) }
        )
    }

    private fun getValue(params: String): String {
        val args = params.split("_")

        if (args.size < 3) {
            return ""
        }

        if (args[0] != "top") {
            return ""
        }

        val skill = Skills.getByID(args[1]) ?: return ""

        val place = args[2].toIntOrNull() ?: return ""

        return when (args.last()) {
            "name" -> skill.getTop(place)?.player?.savedDisplayName
            "amount" -> skill.getTop(place)?.amount?.toString()
            else -> null
        } ?: ""
    }
}
