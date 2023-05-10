package com.willfp.ecoskills.skills

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.placeholder.Placeholder
import com.willfp.eco.core.placeholder.RegistrablePlaceholder
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.util.savedDisplayName
import java.util.regex.Pattern

class EcoSkillsTopPlaceholder(
    private val plugin: EcoPlugin
) : RegistrablePlaceholder {
    private val pattern = Pattern.compile("(top_)[a-z]+_[0-9]+_[a-z]+")

    override fun getPattern(): Pattern = pattern
    override fun getPlugin(): EcoPlugin = plugin

    override fun getValue(params: String, ctx: PlaceholderContext): String? {
        val args = params.split("_")

        if (args.size < 3) {
            return null
        }

        if (args[0] != "top") {
            return null
        }

        val skill = Skills.getByID(args[1]) ?: return null

        val place = args[2].toIntOrNull() ?: return null

        return when (args.last()) {
            "name" -> skill.getTop(place)?.player?.savedDisplayName
            "amount" -> skill.getTop(place)?.level?.toString()
            else -> null
        }
    }
}
