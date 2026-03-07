package com.willfp.ecoskills.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.ecoskills.actionbar.isPersistentActionBarEnabled
import com.willfp.ecoskills.actionbar.sendCompatibleActionBarMessage
import com.willfp.ecoskills.actionbar.togglePersistentActionBar
import com.willfp.ecoskills.plugin
import org.bukkit.entity.Player

object CommandToggleActionBar : Subcommand(
    plugin,
    "toggleactionbar",
    "ecoskills.command.toggleactionbar",
    true
) {

    override fun onExecute(player: Player, args: List<String>) {
        when (player.isPersistentActionBarEnabled) {
            true -> {
                player.sendCompatibleActionBarMessage("")
                player.sendMessage(plugin.langYml.getMessage("disabled-actionbar"))
            }

            false -> player.sendMessage(plugin.langYml.getMessage("enabled-actionbar"))
        }

        player.togglePersistentActionBar()
    }
}
