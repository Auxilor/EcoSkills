package com.willfp.ecoskills.commands

import com.willfp.ecoskills.api.setBaseStatLevel
import com.willfp.ecoskills.api.setMagic
import com.willfp.ecoskills.api.setSkillLevel
import com.willfp.ecoskills.magic.MagicType
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.stats.Stat
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import wiki.ecohub.core.command.CommandBuilder
import wiki.ecohub.game.entity.runOwned

private const val AMOUNT_ARGS = 3

internal fun CommandBuilder<CommandSender>.setCommand() = subcommand(
    name = "set",
    permission = "ecoskills.command.set"
) {
    executes {
        val player = onlinePlayer(0)
        val obj = argument(1, "invalid-skill-stat", "invalid-skill-stat") { skillStatOrMagic(it) }
        val amount = argument(2, "invalid-amount", "invalid-amount") { it.toIntOrNull() }

        if (amount < 0) {
            fail("invalid-amount")
        }

        player.runOwned {
            replyChange(setAndGetReplyKey(player, obj, amount), player, amount, obj)
        }
    }

    completes {
        when (args.size) {
            1 -> playerNames()
            2 -> skillStatAndMagicIds()
            AMOUNT_ARGS -> amountCompletions
            else -> emptyList()
        }
    }
}

private fun setAndGetReplyKey(player: Player, obj: Any, amount: Int): String = when (obj) {
    is Skill -> {
        player.setSkillLevel(obj, amount)
        "set-skill-level"
    }

    is Stat -> {
        player.setBaseStatLevel(obj, amount)
        "set-stat"
    }

    is MagicType -> {
        player.setMagic(obj, amount)
        "set-magic"
    }

    else -> {
        ""
    }
}
