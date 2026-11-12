package com.willfp.ecoskills.commands

import com.willfp.ecoskills.actionbar.isPersistentActionBarEnabled
import com.willfp.ecoskills.actionbar.sendCompatibleActionBarMessage
import com.willfp.ecoskills.actionbar.togglePersistentActionBar
import org.bukkit.command.CommandSender
import wiki.ecohub.core.command.CommandBuilder
import wiki.ecohub.ui.display.actionbar.PersistentActionBars

internal fun CommandBuilder<CommandSender>.toggleActionBarCommand() = playerSubcommand(
    name = "toggleactionbar",
    permission = "ecoskills.command.toggleactionbar"
) {
    executes {
        if (sender.isPersistentActionBarEnabled) {
            sender.sendCompatibleActionBarMessage("")
            reply("disabled-actionbar")
        } else {
            reply("enabled-actionbar")
        }

        sender.togglePersistentActionBar()

        if (sender.isPersistentActionBarEnabled) {
            PersistentActionBars.refresh(sender)
        }
    }
}
