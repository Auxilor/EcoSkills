package com.willfp.ecoskills.actionbar

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.willfp.eco.core.AbstractPacketAdapter
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.StringUtils
import org.bukkit.entity.Player

class ActionBarCompatSetActionBar(
    plugin: EcoPlugin
) : AbstractPacketAdapter(
    plugin,
    PacketType.Play.Server.SET_ACTION_BAR_TEXT,
    false
) {
    override fun onSend(packet: PacketContainer, player: Player, event: PacketEvent) {
        val component = StringUtils.toComponent(
            StringUtils.jsonToLegacy(
                packet.chatComponents.read(0).json
            )
        )

        if (!component.contains(ActionBarUtils.ecoSkillsComponentSignature)) {
            val uuid = player.uniqueId
            ActionBarUtils.blacklist(uuid)
        }
    }
}

class ActionBarCompatChatMessage(
    plugin: EcoPlugin
) : AbstractPacketAdapter(
    plugin,
    PacketType.Play.Server.CHAT,
    false
) {
    override fun onSend(packet: PacketContainer, player: Player, event: PacketEvent) {
        val position = packet.bytes.read(0).toInt()

        if (position != 2) {
            return
        }

        val component = StringUtils.toComponent(
            StringUtils.jsonToLegacy(
                packet.chatComponents.read(0).json
            )
        )

        if (!component.contains(ActionBarUtils.ecoSkillsComponentSignature)) {
            val uuid = player.uniqueId
            ActionBarUtils.blacklist(uuid)
        }
    }
}
