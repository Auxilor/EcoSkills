package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.CommandHandler
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.core.data.PlayerProfile
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.util.NamespacedKeyUtils
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandToggleActionbar(plugin: EcoPlugin) : Subcommand(
    plugin,
    "toggleactionbar",
    "ecoskills.command.toggleactionbar",
    true
) {
    override fun getHandler(): CommandHandler {
        return CommandHandler { sender: CommandSender, _: List<String?>? ->
            if (!plugin.configYml.getBool("persistent-action-bar.enabled")) {
                sender.sendMessage(plugin.langYml.getMessage("actionbar-disabled"))
                return@CommandHandler
            }

            val player = sender as Player
            val profile = PlayerProfile.load(player)

            var currentStatus = profile.read(DESCRIPTIONS_KEY)
            currentStatus = !currentStatus
            profile.write(DESCRIPTIONS_KEY, currentStatus)
            if (currentStatus) {
                player.sendMessage(plugin.langYml.getMessage("enabled-actionbar"))
            } else {
                player.sendMessage(plugin.langYml.getMessage("disabled-actionbar"))
            }
        }
    }

    companion object {
        val DESCRIPTIONS_KEY = PersistentDataKey(
            NamespacedKeyUtils.create("ecoskills", "actionbar_enabled"),
            PersistentDataKeyType.BOOLEAN,
            true
        )
    }
}
