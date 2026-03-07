package com.willfp.ecoskills.skills

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.placeholder.RegistrablePlaceholder
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.util.savedDisplayName
import com.willfp.ecoskills.skills.SkillsLeaderboard.getTop
import java.util.regex.Pattern

object EcoSkillsSkillTopPlaceholder : RegistrablePlaceholder {
    private val pattern = Pattern.compile("top_([a-z0-9_]+)_(\\d+)_(name|level|amount)")

    override fun getPattern(): Pattern = pattern
    override fun getPlugin(): EcoPlugin = com.willfp.ecoskills.plugin

    override fun getValue(params: String, ctx: PlaceholderContext): String? {
        val emptyPosition: String = plugin.langYml.getString("top.empty-position")
        val matcher = pattern.matcher(params)

        if (!matcher.matches()) return null

        val skillId = matcher.group(1) // Skill ID (allows underscores)
        val place = matcher.group(2).toIntOrNull() ?: return null
        val type = matcher.group(3)

        val skill = Skills.getByID(skillId) ?: return null

        return when (type) {
            "name" -> getTop(skill, place)?.player?.savedDisplayName ?: emptyPosition
            "level", "amount" -> getTop(skill, place)?.level?.toString() ?: emptyPosition
            else -> null
        }
    }
}

object EcoSkillsTopPlaceholder : RegistrablePlaceholder {
    private val pattern = Pattern.compile("top_(\\d+)_(name|level|amount)")

    override fun getPattern(): Pattern = pattern
    override fun getPlugin(): EcoPlugin = com.willfp.ecoskills.plugin

    override fun getValue(params: String, ctx: PlaceholderContext): String? {
        val emptyPosition: String = plugin.langYml.getString("top.empty-position")
        val matcher = pattern.matcher(params)

        if (!matcher.matches()) return null

        val place = matcher.group(1).toIntOrNull() ?: return null
        val type = matcher.group(2)

        return when (type) {
            "name" -> Skills.getTop(place)?.player?.savedDisplayName ?: emptyPosition
            "level", "amount" -> Skills.getTop(place)?.level?.toString() ?: emptyPosition
            else -> null
        }
    }
}
