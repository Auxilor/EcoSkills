package com.willfp.ecoskills.commands

import com.willfp.ecoskills.skills.display.isXPGainSoundEnabled
import com.willfp.ecoskills.skills.display.toggleXPGainSound
import org.bukkit.command.CommandSender
import wiki.ecohub.core.command.CommandBuilder

internal fun CommandBuilder<CommandSender>.toggleXpGainSoundCommand() = playerSubcommand(
    name = "togglexpgainsound",
    permission = "ecoskills.command.togglexpgainsound"
) {
    executes {
        if (sender.isXPGainSoundEnabled) {
            reply("disabled-xp-gain-sound")
        } else {
            reply("enabled-xp-gain-sound")
        }

        sender.toggleXPGainSound()
    }
}
