package com.willfp.ecoskills.commands

import com.willfp.ecoskills.magic.MagicType
import com.willfp.ecoskills.magic.MagicTypes
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stat
import com.willfp.ecoskills.stats.Stats
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import wiki.ecohub.core.command.CommandContext
import wiki.ecohub.ui.text.StringUtils
import wiki.ecohub.ui.text.formatEco

internal val amountCompletions = listOf("1", "2", "5", "10", "100")

/**
 * The skill, stat or magic type with [id].
 */
internal fun skillStatOrMagic(id: String): Any? =
    Skills.getByID(id) ?: Stats.getByID(id) ?: MagicTypes.getByID(id)

internal fun skillStatAndMagicIds(): List<String> =
    (Skills.values() union Stats.values() union MagicTypes.values()).map { it.id }

internal fun nameOf(obj: Any): String = when (obj) {
    is Skill -> obj.name
    is Stat -> obj.name
    is MagicType -> obj.name
    else -> "unknown"
}

/**
 * The online player named by argument [index]; missing or offline sends `invalid-player`.
 */
internal fun CommandContext<*>.onlinePlayer(index: Int): Player =
    argument(index, "invalid-player", "invalid-player") { Bukkit.getPlayer(it) }

/**
 * Send the lang message [key] with [player], [amount] and [obj] filled in, then formatted.
 */
internal fun CommandContext<*>.replyChange(key: String, player: Player, amount: Any, obj: Any) {
    sender.sendMessage(
        plugin.langYml.getMessage(key, StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
            .replace(oldValue = "%player%", newValue = player.name)
            .replace(oldValue = "%amount%", newValue = amount.toString())
            .replace(oldValue = "%obj%", newValue = nameOf(obj))
            .formatEco()
    )
}
