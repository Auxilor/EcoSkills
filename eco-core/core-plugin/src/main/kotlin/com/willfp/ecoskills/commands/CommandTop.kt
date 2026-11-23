package com.willfp.ecoskills.commands

import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.skills.Skills
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import wiki.ecohub.core.command.CommandBuilder
import wiki.ecohub.core.command.CommandContext
import wiki.ecohub.data.playerbase.leaderboard.LeaderboardEntry
import wiki.ecohub.data.playerbase.leaderboard.Leaderboards
import wiki.ecohub.game.entity.player.PlayerUtils
import wiki.ecohub.ui.placeholder.placeholderContext
import wiki.ecohub.ui.text.formatEco

private val pageCompletions = listOf("1", "2", "3", "4", "5")

private const val PAGE_SIZE = 10

/**
 * `top [page]` shows the total level leaderboard, `top <skill> [page]` one skill's.
 */
internal fun CommandBuilder<CommandSender>.topCommand() = subcommand(
    name = "top",
    permission = "ecoskills.command.top"
) {
    executes {
        if (!plugin.configYml.getBool("leaderboard.enabled")) {
            fail("invalid-skill")
        }

        val skill = Skills.getByID(args.getOrNull(0))
        val positions = positionsOnPage(pageOf(skill))

        sendTop(
            if (skill == null) {
                positions.mapNotNull { Skills.getTop(it) }
            } else {
                positions.mapNotNull { skill.leaderboard?.getTop(it) }
            }
        )
    }

    completes {
        when {
            !plugin.configYml.getBool("leaderboard.enabled") -> emptyList()
            args.size == 1 -> pageCompletions + Skills.values().map { it.id }
            args.size == 2 && Skills.getByID(args[0]) != null -> pageCompletions
            else -> emptyList()
        }
    }
}

private fun CommandContext<CommandSender>.pageOf(skill: Skill?): Int {
    val pageArg = args.getOrNull(if (skill == null) 0 else 1)

    if (skill == null && pageArg?.toIntOrNull() == null && pageArg?.isBlank() == false) {
        fail("invalid-skill")
    }

    return pageArg?.toIntOrNull() ?: 1
}

private fun positionsOnPage(page: Int): IntRange {
    val offset = (page - 1) * PAGE_SIZE
    return offset + 1..offset + PAGE_SIZE
}

private fun CommandContext<CommandSender>.sendTop(top: List<LeaderboardEntry>) {
    val lines = top.map { entry ->
        plugin.langYml.getString("top-line-format")
            .replace(oldValue = "%rank%", newValue = entry.position.toString())
            .replace(oldValue = "%level%", newValue = entry.value.toInt().toString())
            .replace(oldValue = "%player%", newValue = PlayerUtils.getSavedDisplayName(entry.player))
    }.ifEmpty { listOf(Leaderboards.getNoRecordsMessage(plugin)) }

    val messages = plugin.langYml.getStrings("top.format").toMutableList()
    val linesIndex = messages.indexOf("%lines%")

    if (linesIndex != -1) {
        messages.removeAt(linesIndex)
        messages.addAll(linesIndex, lines)
    }

    val context = placeholderContext(player = sender as? Player)

    messages.forEach { sender.sendMessage(it.formatEco(context)) }
}
