package com.willfp.ecoskills.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.skills.display.isXPGainSoundEnabled
import com.willfp.ecoskills.skills.display.toggleXPGainSound
import org.bukkit.entity.Player

object CommandToggleXpGainSound : Subcommand(
    plugin,
    "togglexpgainsound",
    "ecoskills.command.togglexpgainsound",
    true
) {

    override fun onExecute(player: Player, args: List<String>) {
        when (player.isXPGainSoundEnabled) {
            true -> {
                player.sendMessage(plugin.langYml.getMessage("disabled-xp-gain-sound"))
            }

            false -> player.sendMessage(plugin.langYml.getMessage("enabled-xp-gain-sound"))
        }

        player.toggleXPGainSound()
    }
}
