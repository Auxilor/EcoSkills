package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.util.PlayerUtils
import com.willfp.eco.util.StringUtils
import com.willfp.ecoskills.data.LeaderboardHandler
import com.willfp.ecoskills.getSkillLevel
import com.willfp.ecoskills.hasGainSoundEnabled
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.toggleGainSoundEnabled
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil


class CommandToggleSound(plugin: EcoPlugin) :
    Subcommand(
        plugin,
        "togglesound",
        "ecoskills.command.togglesound",
        true
    ) {

    override fun onExecute(sender: CommandSender, args: List<String>) {
        if (!this.plugin.configYml.getBool("skills.progress.sound.enabled")) {
            sender.sendMessage(this.plugin.langYml.getMessage("xp-gain-sound-disabled"))
            return
        }

        val player = sender as Player
        player.toggleGainSoundEnabled()

        val key = when(player.hasGainSoundEnabled()) {
            true -> "enabled"
            else -> "disabled"
        }

        player.sendMessage(this.plugin.langYml.getMessage("$key-xp-gain-sound"))
    }
}