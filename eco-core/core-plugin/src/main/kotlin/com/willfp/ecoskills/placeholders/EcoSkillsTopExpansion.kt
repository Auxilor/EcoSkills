package com.willfp.ecoskills.placeholders

import com.willfp.eco.util.savedDisplayName
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.skills.Skills
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer

class EcoSkillsTopExpansion(val plugin: EcoSkillsPlugin): PlaceholderExpansion() {
    /**
     * The placeholder identifier of this expansion. May not contain %,
     * {} or _
     *
     * @return placeholder identifier that is associated with this expansion
     */
    override fun getIdentifier(): String {
        return "ecoskills"
    }

    /**
     * The author of this expansion
     *
     * @return name of the author for this expansion
     */
    override fun getAuthor(): String {
        return "Auxilor"
    }

    /**
     * The version of this expansion
     *
     * @return current version of this expansion
     */
    override fun getVersion(): String {
        return "1.0.0"
    }

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        val args = params.split("_")

        if (args.size < 3) {
            return ""
        }

        if (args[0] != "top") {
            return ""
        }

        val skill = Skills.getByID(args[1]) ?: return ""

        val place = args[2].toIntOrNull() ?: return ""

        return when (args.lastOrNull() ?: return "Invalid type: ${args.lastOrNull()}. Must be name/amount") {
            "name" -> skill.getTop(place)?.player?.savedDisplayName ?: ""
            "amount" -> skill.getTop(place)?.amount?.toString() ?: ""
            else -> null
        }
    }
}
