package com.willfp.ecoskills.commands

import com.willfp.ecoskills.api.gainSkillXP
import com.willfp.ecoskills.api.getMagic
import com.willfp.ecoskills.api.giveBaseStatLevel
import com.willfp.ecoskills.api.giveSkillXP
import com.willfp.ecoskills.api.setMagic
import com.willfp.ecoskills.magic.MagicType
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.stats.Stat
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import wiki.ecohub.core.command.CommandBuilder
import wiki.ecohub.game.entity.runOwned

private val booleanCompletions = listOf("true", "false")

private const val AMOUNT_ARGS = 3

private const val ACTION_BAR_ARGS = 4

/**
 * The optional fourth argument shows the XP action bar; it defaults to off as before.
 */
internal fun CommandBuilder<CommandSender>.giveCommand() = subcommand(
    name = "give",
    permission = "ecoskills.command.give"
) {
    executes {
        val player = onlinePlayer(0)
        val obj = argument(1, "invalid-skill-stat-magic", "invalid-skill-stat-magic") { skillStatOrMagic(it) }
        val amount = argument(2, "invalid-amount", "invalid-amount") { it.toDoubleOrNull() }

        if (!(amount >= 0)) {
            fail("invalid-amount")
        }

        val showActionBar = args.getOrNull(3)?.toBooleanStrictOrNull() == true

        player.runOwned {
            replyChange(giveAndGetReplyKey(player, obj, amount, showActionBar), player, amount, obj)
        }
    }

    completes {
        when (args.size) {
            1 -> playerNames()
            2 -> skillStatAndMagicIds()
            AMOUNT_ARGS -> amountCompletions
            ACTION_BAR_ARGS -> booleanCompletions
            else -> emptyList()
        }
    }
}

private fun giveAndGetReplyKey(player: Player, obj: Any, amount: Double, showActionBar: Boolean): String = when (obj) {
    is Skill -> {
        if (showActionBar) {
            player.gainSkillXP(obj, amount)
            "gained-skill-xp"
        } else {
            player.giveSkillXP(obj, amount)
            "gave-skill-xp"
        }
    }

    is Stat -> {
        player.giveBaseStatLevel(obj, amount.toInt())
        "gave-stat"
    }

    is MagicType -> {
        player.setMagic(obj, player.getMagic(obj) + amount.toInt())
        "gave-magic"
    }

    else -> {
        ""
    }
}
