@file:Suppress("DEPRECATION")

package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.util.NamespacedKeyUtils
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandToggleActionbar(plugin: EcoPlugin) : Subcommand(
    plugin,
    "toggleactionbar",
    "ecoskills.command.toggleactionbar",
    true
) {

    override fun onExecute(sender: CommandSender, args: List<String>) {
        if (!plugin.configYml.getBool("persistent-action-bar.enabled")) {
            sender.sendMessage(plugin.langYml.getMessage("actionbar-disabled"))
            return
        }

        val player = sender as Player
        val profile = player.profile

        var currentStatus = profile.read(DESCRIPTIONS_KEY)
        currentStatus = !currentStatus
        profile.write(DESCRIPTIONS_KEY, currentStatus)
        if (currentStatus) {
            player.sendMessage(plugin.langYml.getMessage("enabled-actionbar"))
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, *TextComponent.fromLegacyText(""))
            player.sendMessage(plugin.langYml.getMessage("disabled-actionbar"))
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
