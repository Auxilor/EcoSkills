@file:Suppress("DEPRECATION")

package com.willfp.ecoskills.actionbar

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers
import com.willfp.eco.core.AbstractPacketAdapter
import com.willfp.eco.core.EcoPlugin
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerGameModeChangeEvent

class ActionBarCompatSetActionBar(
    plugin: EcoPlugin
) : AbstractPacketAdapter(
    plugin,
    PacketType.Play.Server.SET_ACTION_BAR_TEXT,
    false
) {
    override fun onSend(packet: PacketContainer, player: Player, event: PacketEvent) {
        ActionBarUtils.blacklist(player.uniqueId)
    }
}

class ActionBarCompatChatMessage(
    plugin: EcoPlugin
) : AbstractPacketAdapter(
    plugin,
    PacketType.Play.Server.CHAT,
    ListenerPriority.LOWEST,
    false
) {
    override fun onSend(packet: PacketContainer, player: Player, event: PacketEvent) {
        val type = packet.chatTypes.read(0)

        if (type != EnumWrappers.ChatType.GAME_INFO) {
            return
        }

        ActionBarUtils.blacklist(player.uniqueId)
    }
}

class ActionBarClearOnGamemode: Listener {
    @EventHandler
    fun handle(event: PlayerGameModeChangeEvent) {
        if (event.newGameMode == GameMode.CREATIVE || event.newGameMode == GameMode.SPECTATOR) {
            event.player.spigot().sendMessage(ChatMessageType.ACTION_BAR, *TextComponent.fromLegacyText(""))
        }
    }
}
